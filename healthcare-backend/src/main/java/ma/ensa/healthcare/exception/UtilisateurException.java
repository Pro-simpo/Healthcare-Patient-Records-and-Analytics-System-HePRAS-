package ma.ensa.healthcare.exception;

/**
 * Exception pour les erreurs liées aux utilisateurs
 */
public class UtilisateurException extends RuntimeException {
    
    public UtilisateurException(String message) {
        super(message);
    }
    
    public UtilisateurException(String message, Throwable cause) {
        super(message, cause);
    }
}