package ma.ensa.healthcare.ui.utils;

import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.model.enums.Role;

/**
 * Gestionnaire de session pour l'utilisateur connecté
 */
public class SessionManager {

    private static Utilisateur currentUser;

    /**
     * Définit l'utilisateur actuellement connecté
     */
    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    /**
     * Retourne l'utilisateur actuellement connecté
     */
    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public static boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Vérifie si l'utilisateur est un médecin
     */
    public static boolean isMedecin() {
        return hasRole(Role.MEDECIN);
    }

    /**
     * Vérifie si l'utilisateur est un réceptionniste
     */
    public static boolean isReceptionniste() {
        return hasRole(Role.RECEPTIONNISTE);
    }

    /**
     * Retourne le nom complet de l'utilisateur connecté
     */
    public static String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getUsername();
        }
        return "Invité";
    }

    /**
     * Retourne le rôle de l'utilisateur connecté
     */
    public static String getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole().name();
        }
        return "NONE";
    }
}