package i18n;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Formatter {

    public static final DateTimeFormatter DATEFORMAT_DE = DateTimeFormatter.ofPattern("d.M.yyyy");

    public static String formatDate(String lang, LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        if ("de".equals(lang)) {
            return dateTime.format(DATEFORMAT_DE);
        }
        return dateTime.toLocalDate().toString();
    }

    public static String formatDate(String lang, LocalDate date) {
        if (date == null) {
            return "";
        }
        if ("de".equals(lang)) {
            return date.format(DATEFORMAT_DE);
        }
        return date.toString();
    }

    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(dateFormatter);
    }

    public static String formatDateTime(String lang, LocalDateTime dateTime) {
        return formatDate(lang, dateTime) + " " + formatTime(dateTime);
    }
}
