package ma.ensa.healthcare.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utilitaires pour la manipulation de chaînes de caractères
 */
public class StringUtils {

    /**
     * Met en majuscule la première lettre
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Met en majuscule la première lettre de chaque mot
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        return Arrays.stream(str.split("\\s+"))
            .map(StringUtils::capitalize)
            .collect(Collectors.joining(" "));
    }

    /**
     * Masque un mot de passe
     */
    public static String maskPassword(String password) {
        if (password == null) {
            return "****";
        }
        return "*".repeat(password.length());
    }

    /**
     * Masque partiellement une chaîne (garde début et fin)
     */
    public static String maskPartial(String str, int visibleStart, int visibleEnd) {
        if (str == null || str.length() <= visibleStart + visibleEnd) {
            return str;
        }
        
        int maskLength = str.length() - visibleStart - visibleEnd;
        return str.substring(0, visibleStart) + 
               "*".repeat(maskLength) + 
               str.substring(str.length() - visibleEnd);
    }

    /**
     * Masque un email (garde première lettre + domaine)
     * Exemple: john.doe@example.com → j*******@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        
        if (local.length() <= 1) {
            return email;
        }
        
        return local.charAt(0) + "*".repeat(local.length() - 1) + "@" + domain;
    }

    /**
     * Masque un CIN (garde 2 premiers et 2 derniers caractères)
     * Exemple: A123456 → A1****56
     */
    public static String maskCin(String cin) {
        if (cin == null || cin.length() <= 4) {
            return cin;
        }
        return maskPartial(cin, 2, 2);
    }

    /**
     * Vérifie si une chaîne est null ou vide
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Vérifie si une chaîne n'est pas vide
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Retourne une chaîne par défaut si null ou vide
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Tronque une chaîne à une longueur max
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * Supprime les accents
     */
    public static String removeAccents(String str) {
        if (str == null) {
            return null;
        }
        
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
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
     * Répète une chaîne n fois
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        return str.repeat(count);
    }

    /**
     * Inverse une chaîne
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Compte les occurrences d'un caractère
     */
    public static int countOccurrences(String str, char c) {
        if (str == null) {
            return 0;
        }
        return (int) str.chars().filter(ch -> ch == c).count();
    }

    /**
     * Génère un slug (URL-friendly)
     * Exemple: "Mon Titre!" → "mon-titre"
     */
    public static String slugify(String str) {
        if (str == null) {
            return null;
        }
        
        return removeAccents(str)
            .toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
    }

    /**
     * Pad à gauche
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        return String.valueOf(padChar).repeat(length - str.length()) + str;
    }

    /**
     * Pad à droite
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        return str + String.valueOf(padChar).repeat(length - str.length());
    }

    /**
     * Formate un numéro de téléphone
     * Exemple: 0612345678 → 06 12 34 56 78
     */
    public static String formatTelephone(String telephone) {
        if (telephone == null) {
            return null;
        }
        
        String cleaned = telephone.replaceAll("[^0-9+]", "");
        
        if (cleaned.startsWith("+212")) {
            // Format international: +212 6 12 34 56 78
            if (cleaned.length() == 13) {
                return "+212 " + cleaned.charAt(4) + " " + 
                       cleaned.substring(5, 7) + " " + 
                       cleaned.substring(7, 9) + " " + 
                       cleaned.substring(9, 11) + " " + 
                       cleaned.substring(11);
            }
        } else if (cleaned.startsWith("0")) {
            // Format national: 06 12 34 56 78
            if (cleaned.length() == 10) {
                return cleaned.substring(0, 2) + " " + 
                       cleaned.substring(2, 4) + " " + 
                       cleaned.substring(4, 6) + " " + 
                       cleaned.substring(6, 8) + " " + 
                       cleaned.substring(8);
            }
        }
        
        return telephone; // Retourner tel quel si format non reconnu
    }

    /**
     * Extrait les initiales
     * Exemple: "Jean Pierre Dupont" → "JPD"
     */
    public static String extractInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        
        return Arrays.stream(fullName.trim().split("\\s+"))
            .filter(word -> !word.isEmpty())
            .map(word -> String.valueOf(word.charAt(0)).toUpperCase())
            .collect(Collectors.joining());
    }
}