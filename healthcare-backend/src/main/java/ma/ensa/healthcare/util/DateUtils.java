package ma.ensa.healthcare.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utilitaires pour la gestion des dates
 * Support LocalDate, LocalDateTime, SQL Date/Timestamp
 */
public class DateUtils {
    
    // Formatters prédéfinis
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FULL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ========== FORMATAGE ==========
    
    /**
     * Formate une LocalDate au format dd/MM/yyyy
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Formate une LocalDateTime au format dd/MM/yyyy HH:mm
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * Formate une LocalDateTime au format complet dd/MM/yyyy HH:mm:ss
     */
    public static String formatFull(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FULL_DATETIME_FORMATTER) : "";
    }

    /**
     * Formate uniquement l'heure HH:mm
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : "";
    }

    /**
     * Formate une date avec un pattern personnalisé
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Formate une date/heure avec un pattern personnalisé
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // ========== PARSING ==========

    /**
     * Parse une chaîne au format dd/MM/yyyy en LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide: " + dateStr + ". Attendu: dd/MM/yyyy", e);
        }
    }

    /**
     * Parse une chaîne au format dd/MM/yyyy HH:mm en LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date/heure invalide: " + dateTimeStr + ". Attendu: dd/MM/yyyy HH:mm", e);
        }
    }

    /**
     * Parse une date ISO (yyyy-MM-dd)
     */
    public static LocalDate parseIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format ISO invalide: " + dateStr + ". Attendu: yyyy-MM-dd", e);
        }
    }

    // ========== CONVERSIONS SQL ==========

    /**
     * Convertit LocalDate en java.sql.Date
     */
    public static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    /**
     * Convertit java.sql.Date en LocalDate
     */
    public static LocalDate fromSqlDate(Date sqlDate) {
        return sqlDate != null ? sqlDate.toLocalDate() : null;
    }

    /**
     * Convertit LocalDateTime en java.sql.Timestamp
     */
    public static Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
    }

    /**
     * Convertit java.sql.Timestamp en LocalDateTime
     */
    public static LocalDateTime fromSqlTimestamp(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    // ========== CALCULS ==========

    /**
     * Calcule l'âge à partir d'une date de naissance
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calcule la différence en jours entre deux dates
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calcule la différence en heures entre deux dates/heures
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calcule la différence en minutes entre deux dates/heures
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    // ========== VÉRIFICATIONS ==========

    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Vérifie si une date est dans le futur
     */
    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Vérifie si une date est aujourd'hui
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Vérifie si une date/heure est dans le passé
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Vérifie si une date/heure est dans le futur
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    // ========== MANIPULATION ==========

    /**
     * Ajoute des jours à une date
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * Ajoute des mois à une date
     */
    public static LocalDate addMonths(LocalDate date, int months) {
        return date != null ? date.plusMonths(months) : null;
    }

    /**
     * Ajoute des années à une date
     */
    public static LocalDate addYears(LocalDate date, int years) {
        return date != null ? date.plusYears(years) : null;
    }

    /**
     * Obtient le premier jour du mois
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(1) : null;
    }

    /**
     * Obtient le dernier jour du mois
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(date.lengthOfMonth()) : null;
    }

    /**
     * Obtient la date actuelle
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Obtient la date/heure actuelle
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Obtient hier
     */
    public static LocalDate yesterday() {
        return LocalDate.now().minusDays(1);
    }

    /**
     * Obtient demain
     */
    public static LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }
}