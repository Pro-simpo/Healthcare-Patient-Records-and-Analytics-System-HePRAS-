package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.exception.ValidationException;

public class Validator {
    public static void notNull(Object obj, String message) {
        if (obj == null) throw new ValidationException(message);
    }

    public static void checkCondition(boolean condition, String message) {
        if (!condition) throw new ValidationException(message);
    }
}