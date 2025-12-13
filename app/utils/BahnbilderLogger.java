package utils;

import play.mvc.Http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPOutputStream;

public class BahnbilderLogger {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");

    private static Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("access.log", true)), "UTF-8");

    public static void logAccess(Http.RequestHeader request) {
        try () {
            writer.write(request.uri() + "\n");
            System.out.println("wrote access log!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
