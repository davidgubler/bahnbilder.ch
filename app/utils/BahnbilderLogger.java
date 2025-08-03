package utils;

import play.mvc.Http;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BahnbilderLogger {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");

    private static void log(Http.RequestHeader request, String message, String level, String callerClass) {
        System.out.println(dateTimeFormatter.format(LocalDateTime.now()) + " " + (request == null ? "0.0.0.0" : request.remoteAddress()) + " " + level + " " + callerClass + " " + message);
    }

    public static void info(Http.RequestHeader request, String message) {
        String callerClass = Thread.currentThread().getStackTrace()[2].getClassName();
        log(request, message, "INFO", callerClass);
    }

    public static void error(Http.RequestHeader request, Throwable e) {
        String callerClass = Thread.currentThread().getStackTrace()[2].getClassName();
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        log(request, message, "ERROR", callerClass);
        e.printStackTrace();
    }
}
