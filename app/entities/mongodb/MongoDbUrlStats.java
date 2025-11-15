package entities.mongodb;

import dev.morphia.annotations.*;
import entities.UrlStats;
import utils.SimpleDigest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Entity(useDiscriminator = false)
public class MongoDbUrlStats implements UrlStats {
    // url
    private String u;

    // request
    private String r;

    // count
    private Integer c;

    public MongoDbUrlStats() {
        // dummy for Morphia
    }

    public MongoDbUrlStats(String url, String referer) {
        this.u = url;
        this.r = referer;
        this.c = 1;
    }

    @Transient
    private String mapKey = null;

    public String getMapKey() {
        if (mapKey == null) {
            // optimize the entropy in 8 characters without resorting to special characters
            String hash = new String(Base64.getEncoder().encode(new SimpleDigest().hash((u + "|" + r).getBytes(StandardCharsets.UTF_8))));
            hash = hash.replace("+", "").replace("/", "");
            if (hash.length() < 7) {
                // HIGHLY unlikely but we might have removed too many characters when removing '+' and '/'
                hash = hash + "0000000";
            }
            // A map key length of 8 results in 62^8 possible buckets, with 1 mio entries that's a 0.25% chance of a collision, we're OK with that
            mapKey = hash.substring(0, 8);
        }
        return mapKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbUrlStats that = (MongoDbUrlStats) o;
        return Objects.equals(u, that.u) && Objects.equals(r, that.r);
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, r);
    }

    public int getCount() {
        return c;
    }

    public String getUrl() {
        return u;
    }

    public String getReferer() {
        return r;
    }
}
