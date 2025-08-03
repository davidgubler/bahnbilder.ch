package i18n;

import play.mvc.Http;
import utils.Config;
import java.util.Objects;

public class Lang {
    public static String get(Http.RequestHeader request) {
        if (!Objects.equals(Config.Option.HOST_DE.get(), Config.Option.HOST_EN.get())) {
            if (Config.Option.HOST_DE.get().equals(request.host())) {
                return "de";
            }
            if (Config.Option.HOST_EN.get().equals(request.host())) {
                return "en";
            }
        }
        if (request.cookie("lang").isPresent()) {
            if ("de".equals(request.cookie("lang").get().value())) {
                return "de";
            }
            if ("en".equals(request.cookie("lang").get().value())) {
                return "en";
            }
        }
        return "de";
    }
}
