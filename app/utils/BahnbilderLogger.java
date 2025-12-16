package utils;

import play.Logger;
import play.mvc.Http;
import play.mvc.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BahnbilderLogger {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");

    private final Logger.ALogger logger;

    public <T> BahnbilderLogger(Class<T> clazz) {
        logger = Logger.of(clazz);
    }

    private String getInfo(Http.RequestHeader request) {
        try {
            // will throw a RuntimeException if there's no context
            return request.remoteAddress() + " ";
        } catch (RuntimeException e) {
            return "127.0.0.1";
        }
    }

    private String getAccessLogLine(Http.RequestHeader request, Result result) {
        String ip = request.remoteAddress();
        String time = "[" + LocalDateTime.now().toString() + "]";
        String req = "\"" + request.method() + " " + request.uri() + " " + request.version() + "\"";
        String status = "" + result.status();
        String bytes = "???";
        String referer = "\"" + request.header("Referer").orElse("") + "\"";
        String agent = "\"" + request.header("User-Agent").orElse("") + "\"";
        return ip + " " + time + " " + req + " " + status + " " + bytes + " " + referer + " " + agent;
    }

    public void access(Http.RequestHeader request, Result result) {
        logger.info(getAccessLogLine(request, result));
    }

    public void info(Http.RequestHeader request, String message) {
        logger.info(getInfo(request) + message);
    }

    public void error(Http.RequestHeader request, Throwable e) {
        logger.error(getInfo(request), e);
    }
}
