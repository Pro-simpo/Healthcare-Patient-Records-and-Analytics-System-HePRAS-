package ma.ensa.healthcare.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }
}