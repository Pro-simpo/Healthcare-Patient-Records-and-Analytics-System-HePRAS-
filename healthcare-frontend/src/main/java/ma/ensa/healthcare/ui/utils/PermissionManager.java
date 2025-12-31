package ma.ensa.healthcare.ui.utils;

import ma.ensa.healthcare.model.enums.Role;

/**
 * Gestionnaire centralisé des permissions par rôle
 */
public class PermissionManager {

    /**
     * Vérifie si l'utilisateur peut accéder à la page Patients
     */
    public static boolean canAccessPatients() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE || 
               role == Role.MEDECIN;
    }

    /**
     * Vérifie si l'utilisateur peut modifier un patient
     */
    public static boolean canModifyPatient() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut supprimer un patient
     */
    public static boolean canDeletePatient() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut accéder aux rendez-vous
     */
    public static boolean canAccessRendezVous() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE || 
               role == Role.MEDECIN;
    }

    /**
     * Vérifie si l'utilisateur peut créer un rendez-vous
     */
    public static boolean canCreateRendezVous() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut modifier/annuler un rendez-vous
     */
    public static boolean canModifyRendezVous() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut confirmer un rendez-vous
     */
    public static boolean canConfirmRendezVous() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut accéder aux consultations
     */
    public static boolean canAccessConsultations() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.MEDECIN;
    }

    /**
     * Vérifie si l'utilisateur peut créer une consultation
     */
    public static boolean canCreateConsultation() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.MEDECIN;
    }

    /**
     * Vérifie si l'utilisateur peut modifier une consultation
     */
    public static boolean canModifyConsultation() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.MEDECIN;
    }

    /**
     * Vérifie si l'utilisateur peut accéder aux factures
     */
    public static boolean canAccessFactures() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut créer une facture
     */
    public static boolean canCreateFacture() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut enregistrer un paiement
     */
    public static boolean canRegisterPayment() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut accéder à la gestion des médecins
     */
    public static boolean canAccessMedecins() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur peut modifier un médecin
     */
    public static boolean canModifyMedecin() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur peut accéder aux paramètres (tous les rôles)
     */
    public static boolean canAccessSettings() {
        return SessionManager.isLoggedIn();
    }

    /**
     * Vérifie si l'utilisateur peut accéder aux paramètres avancés (BDD, système)
     */
    public static boolean canAccessAdvancedSettings() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur peut ajouter des utilisateurs
     */
    public static boolean canAddUser() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur peut exporter des données
     */
    public static boolean canExportData() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN || 
               role == Role.RECEPTIONNISTE;
    }

    /**
     * Vérifie si l'utilisateur peut voir toutes les statistiques
     */
    public static boolean canViewAllStatistics() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur peut voir ses propres statistiques
     */
    public static boolean canViewOwnStatistics() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.MEDECIN;
    }

    /**
     * Retourne un message d'erreur d'accès refusé
     */
    public static String getAccessDeniedMessage() {
        return "Accès refusé : Vous n'avez pas les permissions nécessaires pour effectuer cette action.";
    }

    /**
     * Vérifie si un médecin peut voir uniquement ses propres données
     */
    public static boolean shouldFilterByMedecin() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.MEDECIN;
    }

    /**
     * Retourne l'ID du médecin connecté (si applicable)
     */
    public static Long getConnectedMedecinId() {
        if (!SessionManager.isLoggedIn()) return null;
        
        Role role = SessionManager.getCurrentUser().getRole();
        if (role == Role.MEDECIN && SessionManager.getCurrentUser().getMedecin() != null) {
            return SessionManager.getCurrentUser().getMedecin().getId();
        }
        return null;
    }

    /**
     * Vérifie si un patient peut voir uniquement ses propres données
     */
    public static boolean shouldFilterByPatient() {
        if (!SessionManager.isLoggedIn()) return false;
        
        Role role = SessionManager.getCurrentUser().getRole();
        return role == Role.PATIENT;
    }

    /**
     * Retourne l'ID du patient connecté (si applicable)
     */
    public static Long getConnectedPatientId() {
        if (!SessionManager.isLoggedIn()) return null;
        
        Role role = SessionManager.getCurrentUser().getRole();
        if (role == Role.PATIENT && SessionManager.getCurrentUser().getPatient() != null) {
            return SessionManager.getCurrentUser().getPatient().getId();
        }
        return null;
    }
}