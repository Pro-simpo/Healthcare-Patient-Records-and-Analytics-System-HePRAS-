package ma.ensa.healthcare.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Utilitaires de validation réutilisables
 */
public class ValidationUtils {
    
    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CIN_PATTERN_LETTRE = Pattern.compile("^[A-Z][0-9]{6}$");
    private static final Pattern CIN_PATTERN_CHIFFRES = Pattern.compile("^[0-9]{8}$");
    
    private static final Pattern TELEPHONE_NATIONAL = Pattern.compile("^0[5-7][0-9]{8}$");
    private static final Pattern TELEPHONE_INTERNATIONAL = Pattern.compile("^\\+212[5-7][0-9]{8}$");
    
    private static final Pattern GROUPE_SANGUIN = Pattern.compile("^(A|B|AB|O)[+-]$");

    /**
     * Vérifie si un email est valide
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Vérifie si une chaîne n'est pas vide
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Vérifie si un CIN marocain est valide
     */
    public static boolean isValidCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            return false;
        }
        String cinTrim = cin.trim();
        return CIN_PATTERN_LETTRE.matcher(cinTrim).matches() || 
               CIN_PATTERN_CHIFFRES.matcher(cinTrim).matches();
    }

    /**
     * Vérifie si un téléphone marocain est valide
     */
    public static boolean isValidTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        String telTrim = telephone.trim();
        return TELEPHONE_NATIONAL.matcher(telTrim).matches() || 
               TELEPHONE_INTERNATIONAL.matcher(telTrim).matches();
    }

    /**
     * Vérifie si un groupe sanguin est valide
     */
    public static boolean isValidGroupeSanguin(String groupe) {
        if (groupe == null || groupe.trim().isEmpty()) {
            return false;
        }
        return GROUPE_SANGUIN.matcher(groupe.trim().toUpperCase()).matches();
    }

    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Vérifie si une date est dans le futur
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Vérifie si une date/heure est dans le futur
     */
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Vérifie si un BigDecimal est positif
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Vérifie si un BigDecimal est positif ou zéro
     */
    public static boolean isPositiveOrZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Vérifie si une chaîne contient uniquement des lettres
     */
    public static boolean isAlpha(String str) {
        return str != null && str.matches("^[a-zA-ZÀ-ÿ\\s-]+$");
    }

    /**
     * Vérifie si une chaîne contient uniquement des chiffres
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("^[0-9]+$");
    }

    /**
     * Vérifie si une chaîne est alphanumérique
     */
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Vérifie si une longueur est dans une plage
     */
    public static boolean isLengthBetween(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    /**
     * Vérifie si un nombre est dans une plage
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Vérifie si un nombre est dans une plage
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * Nettoie une chaîne (trim + null si vide)
     */
    public static String clean(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Formate un téléphone marocain au format standard
     */
    public static String formatTelephone(String telephone) {
        if (telephone == null) {
            return null;
        }
        
        String tel = telephone.trim().replaceAll("[^0-9+]", "");
        
        // Si commence par +212, le garder
        if (tel.startsWith("+212")) {
            return tel;
        }
        
        // Si commence par 212, ajouter +
        if (tel.startsWith("212")) {
            return "+" + tel;
        }
        
        // Si commence par 0, format national
        if (tel.startsWith("0")) {
            return tel;
        }
        
        // Sinon, ajouter 0
        return "0" + tel;
    }
}