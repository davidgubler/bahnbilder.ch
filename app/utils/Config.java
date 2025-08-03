package utils;

import org.apache.commons.codec.binary.Hex;
import play.mvc.Http;

import java.time.ZoneId;

public class Config {
    public static final double PHOTO_SPOT_RADIUS_KM = 0.25;

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Zurich");

    public static final int DEFAULT_PHOTO_TYPE_ID = 2;
    public static final int DRONE_PHOTO_TYPE_ID = 3;
    public static final int MAX_PHOTO_SIZE = 1024*1024*15;

    private static final Integer plainPort;
    private static final Integer tlsPort;

    public static final byte[] MAC_SIGNING_KEY;
    static {
        try {
            MAC_SIGNING_KEY = Hex.decodeHex(Option.MAC_SIGNING_KEY.get());
            if (MAC_SIGNING_KEY.length != 64) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Environment variable MAC_SIGNING_KEY must be set to a 512 bit (64 byte) HEX string");
        }
        String plainPortProperty = System.getProperty("http.port");
        plainPort = "disabled".equals(plainPortProperty) ? null : (InputUtils.toInt(plainPortProperty) == null ? 9000 : InputUtils.toInt(plainPortProperty));
        tlsPort = InputUtils.toInt(System.getProperty("https.port"));
    }

    public enum Option {
        MAIL_FROM,
        MAIL_HOSTNAME, // used by conf/application.conf
        MAIL_USERNAME, // used by conf/application.conf
        MAIL_PASSWORD, // used by conf/application.conf
        MAIL_PORT,     // used by conf/application.conf
        CALENDAR_EMAIL,
        CALENDAR_ORDER_AVAILABLE,
        CALENDAR_YEAR,
        CALENDAR_PRICE_CHF,
        CALENDAR_PRICE_EUR,
        CALENDAR_SHIPPING_CHF,
        CALENDAR_SHIPPING_EUR,
        HOST_DE,
        HOST_EN,
        GOOGLE_MAPS_JS_KEY,
        GOOGLE_MAPS_SERVER_KEY,
        LIVEFILES_HOSTNAME,
        MAC_SIGNING_KEY,
        MONGO_HOSTS,
        MONGO_USERNAME,
        MONGO_PASSWORD,
        MONGO_AUTH_DB,
        RAILINFO_HOSTNAME,
        TLS_CERT_CHAIN,
        TLS_PRIVATE_KEY,
        TLS_CERT_CHAIN_FILE,
        TLS_PRIVATE_KEY_FILE;

        public String get() {
            return System.getenv().get(this.name());
        }

        public Integer getInt() {
            try {
                return Integer.parseInt(get());
            } catch (Exception e) {
                return null;
            }
        }

        public boolean getBool() {
            return Boolean.TRUE.toString().equalsIgnoreCase(get());
        }
    }

    public static Integer getPlainPort() {
        return plainPort;
    }

    public static Integer getTLSPort() {
        return tlsPort;
    }

    public static boolean tlsEnabled() {
        return tlsPort != null;
    }

    public static String getSelfUrl(Http.Request request) {
        String protocol = request.secure() ? "https://" : "http://";
        String host = request.host();
        return protocol + host;
    }

    public static String getSelfHost(String lang) {
        String host = Config.Option.HOST_DE.get();
        if ("en".equals(lang)) {
            host = Config.Option.HOST_EN.get();
        }
        return host == null ? ("localhost:" + (tlsEnabled() ? tlsPort : plainPort)) : host;
    }

    public static String getSelfUrl(String lang) {
        String host = getSelfHost(lang);
        return (tlsEnabled() ? "https://" : "http://") + host;
    }
}
