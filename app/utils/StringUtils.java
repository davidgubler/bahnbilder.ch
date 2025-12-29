package utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StringUtils {
    public static String join(List<? extends Object> objectList, String separator) {
        if (objectList == null || objectList.isEmpty()) {
            return "";
        }
        String string = objectList.get(0).toString();
        for (int i = 1; i < objectList.size(); i++) {
            string += separator;
            string += objectList.get(i);
        }
        return string;
    }

    public static String formatSeconds(int totalSeconds) {
        int hours = totalSeconds/3600;
        int minutes = (totalSeconds - hours*3600) / 60;
        int seconds = totalSeconds % 60;
        String s = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        if (hours > 0) {
            s = hours + ":" + s;
        }
        return s;
    }

    private static DateTimeFormatter hourMinuteFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static DateTimeFormatter hourMinuteSecondFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static DateTimeFormatter deDate = DateTimeFormatter.ofPattern("d.M.yyyy");

    public static String formatYearMonth(String lang, LocalDate date) {
        return date == null ? "" : date.format(yearMonthFormatter.localizedBy("de".equals(lang) ? Locale.GERMAN : Locale.ENGLISH));
    }

    public static String formatDate(String lang, LocalDate date) {
        return date == null ? "" : ("de".equals(lang) ? date.format(deDate) : date.toString());
    }

    public static String formatDate(String lang, LocalDateTime date) {
        return date == null ? "" : formatDate(lang, date.toLocalDate());
    }

    public static String formatTime(String lang, LocalDateTime date) {
        return date == null ? "" : date.toLocalTime().format(hourMinuteFormatter);
    }

    public static long formatTimeEpochSecond(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.atZone(zoneId).toEpochSecond();
    }

    public static String normalizeName(String name) {
        return Normalizer.normalize(name.toLowerCase(Locale.ENGLISH), Normalizer.Form.NFKD).replaceAll("\\p{M}", "").replaceAll("[^a-z0-9]", "");
    }

    public static String limitText(String text, int length) {
        if (text == null) {
            return null;
        }
        if (text.length() <= length) {
            return text;
        }
        text = text.substring(0, 280); // FIXME this will happily break characters outside of the BMP due to Java's use of UTF-16
        int lastSpacePos = text.lastIndexOf(" ");
        if (lastSpacePos > 0) {
            text = text.substring(0, lastSpacePos);
        }
        return text + "...";
    }
}
