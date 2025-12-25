package ma.ensa.healthcare.exception;

/**
 * Exception pour les erreurs liées aux rendez-vous
 */
public class RendezVousException extends RuntimeException {
    
    public RendezVousException(String message) {
        super(message);
    }
    
    // ✅ AJOUTER ce constructeur
    public RendezVousException(String message, Throwable cause) {
        super(message, cause);
    }
}