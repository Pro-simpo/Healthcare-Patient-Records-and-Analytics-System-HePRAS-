package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.UtilisateurDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IUtilisateurDAO;
import ma.ensa.healthcare.exception.UtilisateurException;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.model.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour la gestion des utilisateurs et authentification
 */
public class UtilisateurService {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);
    private static final int MAX_TENTATIVES = 3;
    
    private final IUtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    /**
     * Authentification d'un utilisateur
     */
    public Utilisateur login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new UtilisateurException("Le nom d'utilisateur est requis");
        }
        
        if (password == null || password.isEmpty()) {
            throw new UtilisateurException("Le mot de passe est requis");
        }
        
        Utilisateur u = utilisateurDAO.findByUsername(username);
        
        if (u == null) {
            logger.warn("Tentative de connexion avec un utilisateur inexistant : {}", username);
            throw new UtilisateurException("Identifiants incorrects");
        }
        
        // Vérifier si le compte est suspendu
        if ("SUSPENDU".equals(u.getStatut())) {
            logger.warn("Tentative de connexion sur un compte suspendu : {}", username);
            throw new UtilisateurException("Compte suspendu. Contactez l'administrateur.");
        }
        
        // Vérifier si le compte est inactif
        if ("INACTIF".equals(u.getStatut())) {
            logger.warn("Tentative de connexion sur un compte inactif : {}", username);
            throw new UtilisateurException("Compte désactivé");
        }
        
        // Vérifier le mot de passe
        // ⚠️ En production, utiliser BCrypt : BCrypt.checkpw(password, u.getPasswordHash())
        if (u.getPasswordHash().equals(password)) {
            // Connexion réussie
            logger.info("Connexion réussie pour l'utilisateur : {}", username);
            
            // Mettre à jour dernière connexion et réinitialiser tentatives
            utilisateurDAO.updateDerniereConnexion(u.getId());
            
            return u;
        } else {
            // Mot de passe incorrect
            logger.warn("Mot de passe incorrect pour l'utilisateur : {}", username);
            
            // Incrémenter tentatives échouées
            utilisateurDAO.incrementerTentativesEchec(u.getId());
            
            // Vérifier si le compte doit être bloqué
            if (u.getTentativesEchec() != null && u.getTentativesEchec() >= MAX_TENTATIVES - 1) {
                utilisateurDAO.bloquerUtilisateur(u.getId());
                logger.warn("Compte bloqué après {} tentatives : {}", MAX_TENTATIVES, username);
                throw new UtilisateurException("Compte bloqué après trop de tentatives échouées");
            }
            
            throw new UtilisateurException("Identifiants incorrects");
        }
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    public Utilisateur inscrire(Utilisateur u) {
        validateUtilisateur(u);
        
        // Vérifier username unique
        Utilisateur existant = utilisateurDAO.findByUsername(u.getUsername());
        if (existant != null) {
            throw new UtilisateurException("Le nom d'utilisateur " + u.getUsername() + " est déjà utilisé");
        }
        
        // Vérifier email unique
        List<Utilisateur> tousUtilisateurs = utilisateurDAO.findAll();
        boolean emailExiste = tousUtilisateurs.stream()
            .anyMatch(user -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExiste) {
            throw new UtilisateurException("L'email " + u.getEmail() + " est déjà utilisé");
        }
        
        // ⚠️ En production, hasher le mot de passe avec BCrypt
        // u.setPasswordHash(BCrypt.hashpw(u.getPasswordHash(), BCrypt.gensalt()));
        
        // Initialiser statut et date
        if (u.getStatut() == null || u.getStatut().trim().isEmpty()) {
            u.setStatut("ACTIF");
        }
        
        if (u.getDateCreation() == null) {
            u.setDateCreation(LocalDate.now());
        }
        
        if (u.getTentativesEchec() == null) {
            u.setTentativesEchec(0);
        }
        
        try {
            Utilisateur saved = utilisateurDAO.save(u);
            logger.info("Utilisateur inscrit avec succès : {} ({})", saved.getUsername(), saved.getRole());
            return saved;
        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription de l'utilisateur", e);
            throw new UtilisateurException("Impossible d'inscrire l'utilisateur", e);
        }
    }

    /**
     * Récupère un utilisateur par ID
     */
    public Utilisateur getUtilisateurById(Long id) {
        if (id == null) {
            throw new UtilisateurException("L'ID ne peut pas être null");
        }
        
        Utilisateur u = utilisateurDAO.findById(id);
        if (u == null) {
            throw new UtilisateurException("Utilisateur introuvable avec l'ID " + id);
        }
        return u;
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurDAO.findAll();
    }

    /**
     * Met à jour un utilisateur
     */
    public void updateUtilisateur(Utilisateur u) {
        if (u.getId() == null) {
            throw new UtilisateurException("L'ID de l'utilisateur est requis pour la mise à jour");
        }
        
        validateUtilisateur(u);
        
        try {
            utilisateurDAO.update(u);
            logger.info("Utilisateur mis à jour : {} (ID: {})", u.getUsername(), u.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur", e);
            throw new UtilisateurException("Impossible de mettre à jour l'utilisateur", e);
        }
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changerMotDePasse(Long id, String ancienPassword, String nouveauPassword) {
        Utilisateur u = getUtilisateurById(id);
        
        // Vérifier ancien mot de passe
        // ⚠️ En production : BCrypt.checkpw(ancienPassword, u.getPasswordHash())
        if (!u.getPasswordHash().equals(ancienPassword)) {
            throw new UtilisateurException("L'ancien mot de passe est incorrect");
        }
        
        // Valider nouveau mot de passe
        validerMotDePasse(nouveauPassword);
        
        // ⚠️ En production : hasher avec BCrypt
        // String hashedPassword = BCrypt.hashpw(nouveauPassword, BCrypt.gensalt());
        utilisateurDAO.updatePassword(id, nouveauPassword);
        
        logger.info("Mot de passe changé pour utilisateur ID: {}", id);
    }

    /**
     * Réinitialise le mot de passe (par admin)
     */
    public void reinitialiserMotDePasse(Long id, String nouveauPassword) {
        validerMotDePasse(nouveauPassword);
        
        // ⚠️ En production : hasher avec BCrypt
        utilisateurDAO.updatePassword(id, nouveauPassword);
        
        logger.info("Mot de passe réinitialisé pour utilisateur ID: {}", id);
    }

    /**
     * Active un utilisateur
     */
    public void activerUtilisateur(Long id) {
        Utilisateur u = getUtilisateurById(id);
        u.setStatut("ACTIF");
        utilisateurDAO.update(u);
        logger.info("Utilisateur activé : ID {}", id);
    }

    /**
     * Désactive un utilisateur
     */
    public void desactiverUtilisateur(Long id) {
        Utilisateur u = getUtilisateurById(id);
        u.setStatut("INACTIF");
        utilisateurDAO.update(u);
        logger.info("Utilisateur désactivé : ID {}", id);
    }

    /**
     * Débloque un utilisateur suspendu
     */
    public void debloquerUtilisateur(Long id) {
        utilisateurDAO.debloquerUtilisateur(id);
        logger.info("Utilisateur débloqué : ID {}", id);
    }

    /**
     * Supprime un utilisateur
     */
    public void supprimerUtilisateur(Long id) {
        if (id == null) {
            throw new UtilisateurException("ID invalide");
        }
        
        try {
            utilisateurDAO.delete(id);
            logger.info("Utilisateur supprimé : ID {}", id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur", e);
            throw new UtilisateurException("Impossible de supprimer l'utilisateur", e);
        }
    }

    /**
     * Validation complète d'un utilisateur
     */
    private void validateUtilisateur(Utilisateur u) {
        if (u == null) {
            throw new UtilisateurException("L'utilisateur ne peut pas être null");
        }
        
        // 1. Username obligatoire
        if (u.getUsername() == null || u.getUsername().trim().isEmpty()) {
            throw new UtilisateurException("Le nom d'utilisateur est obligatoire");
        }
        
        // Username doit avoir au moins 3 caractères
        if (u.getUsername().length() < 3) {
            throw new UtilisateurException("Le nom d'utilisateur doit contenir au moins 3 caractères");
        }
        
        // 2. Password obligatoire (pour création)
        if (u.getId() == null && (u.getPasswordHash() == null || u.getPasswordHash().isEmpty())) {
            throw new UtilisateurException("Le mot de passe est obligatoire");
        }
        
        // Valider mot de passe si fourni
        if (u.getPasswordHash() != null && !u.getPasswordHash().isEmpty()) {
            validerMotDePasse(u.getPasswordHash());
        }
        
        // 3. Email obligatoire
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            throw new UtilisateurException("L'email est obligatoire");
        }
        
        // Email valide
        if (!u.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UtilisateurException("Format d'email invalide");
        }
        
        // 4. Rôle obligatoire
        if (u.getRole() == null) {
            throw new UtilisateurException("Le rôle est obligatoire");
        }
        
        // 5. Contrainte : un utilisateur ne peut être lié qu'à un seul profil (médecin OU patient)
        if (u.getMedecin() != null && u.getPatient() != null) {
            throw new UtilisateurException("Un utilisateur ne peut pas être à la fois médecin et patient");
        }
    }

    /**
     * Valide la complexité d'un mot de passe
     */
    private void validerMotDePasse(String password) {
        if (password == null || password.length() < 6) {
            throw new UtilisateurException("Le mot de passe doit contenir au moins 6 caractères");
        }
        
        // En production, ajouter des règles plus strictes :
        // - Au moins une majuscule
        // - Au moins un chiffre
        // - Au moins un caractère spécial
    }
}