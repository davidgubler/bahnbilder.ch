package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.Operator;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(value = "operators", useDiscriminator = false)
public class MongoDbOperator implements MongoDbEntity, Operator {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private String abbr;

    private String name;

    private List<String> wikiDataIds;

    public MongoDbOperator() {
        // dummy for Morphia
    }

    public MongoDbOperator(int id, String name, String abbr, List<String> wikiDataIds) {
        this.numId = id;
        this.name = name;
        this.abbr = abbr;
        this.wikiDataIds = wikiDataIds;
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
    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getWikiDataIds() {
        return wikiDataIds == null ? Collections.emptyList() : wikiDataIds;
    }

    public void setWikiDataIds(List<String> wikiDataIds) {
        this.wikiDataIds = wikiDataIds;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbOperator that = (MongoDbOperator) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_id);
    }
}
