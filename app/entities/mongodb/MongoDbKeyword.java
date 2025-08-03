package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.Keyword;
import org.bson.types.ObjectId;

import java.util.*;

@Entity(value = "keywords", useDiscriminator = false)
public class MongoDbKeyword implements MongoDbEntity, Keyword {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private Map<String, String> names = new HashMap<>();

    private List<String> labels;

    public MongoDbKeyword() {
        // dummy for Morphia
    }

    public MongoDbKeyword(int id, String nameDe, String nameEn, List<String> labels) {
        this.numId = id;
        this.names.put("de", nameDe);
        this.names.put("en", nameEn);
        this.labels = new ArrayList<>(labels);
        Collections.sort(this.labels);
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
    public Set<String> getLanguages() {
        return names.keySet();
    }

    @Override
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        if (labels == null) {
            this.labels = null;
        } else {
            this.labels = new ArrayList<>(labels);
            Collections.sort(this.labels);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbKeyword that = (MongoDbKeyword) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_id);
    }

    @Override
    public String toString() {
        return names.get("en");
    }
}
