package ma.ensa.healthcare.exception;

public class HealthcareException extends RuntimeException {
    public HealthcareException(String message) {
        super(message);
    }
    public HealthcareException(String message, Throwable cause) {
        super(message, cause);
    }
}