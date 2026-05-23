package entities.mongodb;

import dev.morphia.annotations.*;
import entities.Country;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static dev.morphia.utils.IndexType.TEXT;

@Entity(value = "countries", useDiscriminator = false)
@Indexes({
        @Index(fields = { @Field(value = "names.de", type = TEXT), @Field(value = "names.en", type = TEXT) }),
        @Index(fields = @Field(value = "numId"), options =  @IndexOptions(unique = true)),
        @Index(fields = @Field(value = "code"), options =  @IndexOptions(unique = true)),
})
public class MongoDbCountry implements MongoDbEntity, Country {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    @Indexed(options = @IndexOptions(unique = true))
    private String code;

    private Map<String, String> names = new HashMap<>();

    private Float searchScore;

    public MongoDbCountry() {
        // dummy for Morphia
    }

    public MongoDbCountry(int id, String code, String nameDe, String nameEn) {
        this.numId = id;
        this.code = code.toUpperCase(Locale.ENGLISH);
        this.names.put("de", nameDe);
        this.names.put("en", nameEn);
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public int getId() {
        return numId;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName(String lang) {
        return names.get(lang);
    }

    public void setName(String lang, String name) {
        this.names.put(lang, name);
    }

    public Map<String, String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return getCode().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbCountry that = (MongoDbCountry) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    public Float getSearchScore() {
        return searchScore;
    }
}
