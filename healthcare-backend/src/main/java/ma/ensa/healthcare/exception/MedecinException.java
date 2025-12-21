package ma.ensa.healthcare.exception;

public class MedecinException extends RuntimeException {

    public MedecinException(String message) {
        super(message);
    }

    public MedecinException(String message, Throwable cause) {
        super(message, cause);
    }
}