package utils;

import i18n.Formatter;
import play.mvc.Http;

import javax.mail.internet.InternetAddress;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class InputUtils {
    public static Map<String, String> NOERROR = new HashMap<>();

    public static String trimToNull(String[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        return trimToNull(input[0]);
    }

    public static String trimToNull(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input;
    }

    public static Double toDouble(String[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        return toDouble(input[0]);
    }

    public static Double toDouble(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        input = input.trim();
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(String[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        return toLong(input[0]);
    }

    public static Long toLong(String input) {
        if (input.isEmpty()) {
            return null;
        }
        input = input.trim();
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer toInt(String[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        return toInt(input[0]);
    }

    public static Integer toInt(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        input = input.trim();
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean toBoolean(String input) {
        return "1".equals(input) || Boolean.TRUE.toString().equalsIgnoreCase(input);
    }

    public static boolean toBoolean(String[] input) {
        return input != null && input.length > 0 && toBoolean(input[0]);
    }

    public static List<String> toListOfStrings(String input, String separatorRegex) {
        if (input == null) {
            return null;
        }
        List<String> strings = Arrays.stream(input.split(separatorRegex)).map(s -> s.trim()).filter(s -> !s.isBlank()).collect(Collectors.toList());
        return strings.isEmpty() ? null : strings;
    }

    public static List<String> toListOfStrings(String[] input, String separatorRegex) {
        if (input == null || input.length == 0) {
            return null;
        }
        return toListOfStrings(input[0], separatorRegex);
    }

    public static List<Integer> toListOfIntegers(String input, String separatorRegex) {
        List<String> listOfStrings = toListOfStrings(input, separatorRegex);
        if (listOfStrings == null) {
            return null;
        }
        List<Integer> integers = new ArrayList<>();
        listOfStrings.forEach(s -> {
            try {
                integers.add(Integer.parseInt(s));
            } catch (Exception e) {
                // skip
            }
        });
        return integers.isEmpty() ? null : integers;
    }

    public static void validateString(String input, String name, Map<String, String> errors) {
        validateString(input, name, true, errors);
    }

    public static void validateString(String input, String name, boolean required, Map<String, String> errors) {
        if (required && input == null || input.isBlank()) {
            errors.put(name, ErrorMessages.MISSING_VALUE);
        }
    }

    public static void validateEmail(String email, String name, boolean required, Map<String, String> errors) {
        if (required && email == null || email.isBlank()) {
            errors.put(name, ErrorMessages.MISSING_VALUE);
            return;
        }
        try {
            new InternetAddress(email, true);
        } catch (Exception e) {
            errors.put(name, ErrorMessages.INVALID_EMAIL);
        }
        if (!email.contains("@")) {
            errors.put(name, ErrorMessages.INVALID_EMAIL);
        }
    }

    public static void validateObject(Object object, String name, boolean required, Map<String, String> errors) {
        if (required && object == null) {
            errors.put(name, ErrorMessages.MISSING_VALUE);
        }
    }

    public static void validateUrl(String url, String name, boolean required, Map<String, String> errors) {
        if (!required && url.isBlank()) {
            return;
        }
        try {
            new URL(url);
        } catch (Exception e) {
            errors.put(name, ErrorMessages.INVALID_URL);
        }
    }

    public static void validateInt(Integer integer, String name, boolean required, Integer minValue, Integer maxValue, Map<String, String> errors) {
        if (integer == null) {
            if (required) {
                errors.put(name, ErrorMessages.MISSING_VALUE);
            }
            return;
        }
        if (minValue != null && integer < minValue) {
            errors.put(name, ErrorMessages.MIN_VALUE_IS("" + minValue));
            return;
        }
        if (maxValue != null && integer > maxValue) {
            errors.put(name, ErrorMessages.MAX_VALUE_IS("" + maxValue));
            return;
        }
    }

    public static LocalDate validateDate(String dateStr, String name, boolean required, Map<String, String> errors) {
        if (dateStr == null || dateStr.isBlank()) {
            if (required) {
                errors.put(name, ErrorMessages.MISSING_VALUE);
            }
            return null;
        }
        LocalDate date = parseDate(dateStr);
        if (date == null) {
            errors.put(name, ErrorMessages.INVALID_VALUE);
        }
        return date;
    }

    public static LocalTime validateTime(String timeStr, String name, boolean required, Map<String, String> errors) {
        if (timeStr == null || timeStr.isBlank()) {
            if (required) {
                errors.put(name, ErrorMessages.MISSING_VALUE);
            }
            return null;
        }
        LocalTime time = parseTime(timeStr);
        if (time == null) {
            errors.put(name, ErrorMessages.INVALID_VALUE);
        }
        return time;
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            try {
                return LocalDate.parse(dateStr, Formatter.DATEFORMAT_DE);
            } catch (Exception f) {
                // all formats failed, too bad
            }
        }
        return null;
    }

    public static LocalDate parseDate(String[] dateStr) {
        if (dateStr == null || dateStr.length == 0) {
            return null;
        }
        return parseDate(dateStr[0]);
    }

    public static LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            // too bad
        }
        return null;
    }

    public static String sanitizeReturnUrl(String returnUrl) {
        returnUrl = trimToNull(returnUrl);
        if (returnUrl == null) {
            return "/";
        }
        if (!returnUrl.startsWith("/")) {
            return "/";
        }
        return returnUrl;
    }

    public static List<String> parseTokens(String tokenString) {
        if (tokenString == null) {
            return null;
        }
        List<String> tokens = Arrays.stream(tokenString.split(",")).map(String::trim).toList();
        return tokens.isEmpty() ? null : tokens;
    }

    public static boolean isBot(Http.RequestHeader req) {
        Optional<String> userAgent = req.header("User-Agent");
        if (userAgent.isPresent()) {
            String ua = userAgent.get().toLowerCase(Locale.ROOT);
            if (ua.contains("bot") ||
                ua.contains("spider") ||
                ua.contains("crawler") ||
                ua.contains("cloudflare") ||
                ua.contains("yahoo") ||
                ua.contains("slurp") ||
                ua.contains("bing") ||
                ua.contains("baidu") ||
                ua.contains("google") ||
                ua.contains("facebook") ||
                ua.contains("yandex") ||
                ua.contains("chatgpt") ||
                ua.contains("openai") ||
                ua.contains("mediapartners")) {
                return true;
            }
        }
        return false;
    }
}
