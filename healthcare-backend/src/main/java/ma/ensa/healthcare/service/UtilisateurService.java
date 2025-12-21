package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.UtilisateurDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IUtilisateurDAO;
import ma.ensa.healthcare.model.Utilisateur;

public class UtilisateurService {
    private final IUtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    public Utilisateur login(String username, String password) {
        Utilisateur u = utilisateurDAO.findByUsername(username);
        if (u != null && u.getPassword().equals(password)) { // Dans un vrai projet, utilisez BCrypt
            if (!u.isActif()) {
                throw new RuntimeException("Compte désactivé");
            }
            return u;
        }
        throw new RuntimeException("Identifiants incorrects");
    }

    public Utilisateur inscrire(Utilisateur u) {
        // Logique de validation (doublons, complexité password, etc.)
        return utilisateurDAO.save(u);
    }
}