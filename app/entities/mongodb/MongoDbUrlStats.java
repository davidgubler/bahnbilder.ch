package entities.mongodb;

import dev.morphia.annotations.*;
import utils.SimpleDigest;

import java.util.Objects;

@Entity(useDiscriminator = false)
public class MongoDbUrlStats {
    private String url;
    private String ref;
    private Integer cnt;

    public MongoDbUrlStats() {
        // dummy for Morphia
    }

    public MongoDbUrlStats(String url, String referer) {
        this.url = url;
        this.ref = referer;
        this.cnt = 1;
    }

    public String getMapKey() {
        return new SimpleDigest().hash(url + "|" + ref).substring(0, 12);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbUrlStats that = (MongoDbUrlStats) o;
        return Objects.equals(url, that.url) && Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, ref);
    }
}
