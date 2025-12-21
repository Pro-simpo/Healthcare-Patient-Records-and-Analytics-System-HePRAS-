package ma.ensa.healthcare.util;

public class StringUtils {
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String maskPassword(String password) {
        return (password == null) ? "****" : "*".repeat(password.length());
    }
}