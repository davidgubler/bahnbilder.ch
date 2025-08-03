package entities.mongodb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.morphia.annotations.*;
import entities.Wikidata;
import entities.wikidata.*;
import org.bson.types.ObjectId;
import utils.SimpleDigest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity(value = "wikidata", useDiscriminator = false)
public class MongoDbWikidata implements MongoDbEntity, Wikidata {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private String ref;

    private String data;

    private Instant lastRefresh;

    @Transient
    private WikiDataEntityData entityData;

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public MongoDbWikidata() {
        // dummy for Morphia
    }

    public MongoDbWikidata(String ref, String data) {
        this.ref = ref;
        this.data = data;
        this.lastRefresh = Instant.now();
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public String getData() {
        return data;
    }

    private WikiDataEntityData getEntityData() {
        if (entityData == null) {
            try {
                entityData = MAPPER.readValue(data, new TypeReference<>() {});
            } catch (Exception e) {
                entityData = null;
            }
        }
        return entityData;
    }

    public void setData(String data) {
        this.data = data;
        this.lastRefresh = Instant.now();
    }

    @Override
    public boolean needsRefresh() {
        return lastRefresh.isBefore(Instant.now().minus(30, ChronoUnit.DAYS));
    }

    public Instant getLastRefresh() {
        return lastRefresh;
    }

    @Transient
    private LocalDate incepted;

    @Override
    public LocalDate getIncepted() {
        if (incepted == null) {
            try {
                // P571 = inception
                return ((WikiDataSnakDatavalueTime) getEntityData().entities.get(ref).claims.get("P571").get(0).mainsnak.datavalue).value.getDate();
            } catch (Exception e) {
                // try something else
            }
            try {
                // P1619 = date of official opening
                return ((WikiDataSnakDatavalueTime) getEntityData().entities.get(ref).claims.get("P1619").get(0).mainsnak.datavalue).value.getDate();
            } catch (Exception e) {
                return null;
            }
        }
        return incepted;
    }

    @Override
    public Integer getInceptedYear() {
        return getIncepted() == null ? null : getIncepted().getYear();
    }

    @Transient
    private LocalDate dissolved;

    @Override
    public LocalDate getDissolved() {
        if (dissolved == null) {
            try {
                // P576 = dissolved
                return ((WikiDataSnakDatavalueTime) getEntityData().entities.get(ref).claims.get("P576").get(0).mainsnak.datavalue).value.getDate();
            } catch (Exception e) {
                // try something else
            }
            try {
                // P1366 = replaced by
                // P585 = point in time
                return ((WikiDataSnakDatavalueTime) getEntityData().entities.get(ref).claims.get("P1366").get(0).qualifiers.get("P585").get(0).datavalue).value.getDate();
            } catch (Exception e) {
                // try something else
            }
            try {
                // P31 = instance of
                // P582 = end time
                return ((WikiDataSnakDatavalueTime) getEntityData().entities.get(ref).claims.get("P31").get(0).qualifiers.get("P582").get(0).datavalue).value.getDate();
            } catch (Exception e) {
                return null;
            }
        }
        return dissolved;
    }

    @Override
    public Integer getDissolvedYear() {
        return getDissolved() == null ? null : getDissolved().plusDays(-1).getYear();
    }


    private String getLogo() {
        try {
            // P154 = logo
            return ((WikiDataSnakDatavalueString)getEntityData().entities.get(ref).claims.get("P154").get(0).mainsnak.datavalue).value.replace(" ", "_");
        } catch (Exception e) {
            return null;
        }
    }

    public String getLogoUrl() {
        if (getLogo() == null) {
            return null;
        }
        SimpleDigest sd = new SimpleDigest();
        String md5 = sd.hash(getLogo());
        String dir = md5.substring(0, 1) + "/" + md5.substring(0, 2);
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/" + dir + "/" + getLogo() + "/500px-" + getLogo() + ".png";
    }

    public String getLogoSrc() {
        return getLogo() == null ? null : "https://commons.wikimedia.org/wiki/File:" + getLogo();
    }

    @Override
    public String getName(String lang) {
        try {
            for (String l : List.of(lang, "en", "de", "mul", "fr", "it")) {
                if (getEntityData().entities.get(ref).labels.containsKey(lang)) {
                    return getEntityData().entities.get(ref).labels.get(lang).value;
                }
            }
            return getEntityData().entities.get(ref).labels.entrySet().iterator().next().getValue().value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int compareTo(Wikidata wikidata) {
        if (this.getIncepted() == null || wikidata.getIncepted() == null || this.getIncepted().equals(wikidata.getIncepted())) {
            return this.ref.compareTo(wikidata.getRef());
        }
        return this.getIncepted().compareTo(wikidata.getIncepted());
    }
}
