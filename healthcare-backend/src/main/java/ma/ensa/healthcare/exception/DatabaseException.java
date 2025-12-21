package ma.ensa.healthcare.exception;

public class DatabaseException extends HealthcareException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}