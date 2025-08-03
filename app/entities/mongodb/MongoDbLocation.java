package entities.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.Location;
import org.bson.types.ObjectId;

@Entity(value = "locations", useDiscriminator = false)
public class MongoDbLocation implements MongoDbEntity, Location {
    @JsonIgnore
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    @Indexed(options = @IndexOptions(unique = true))
    private String name;

    public MongoDbLocation() {
        // dummy for Morphia
    }

    public MongoDbLocation(int id, String name) {
        this.numId = id;
        this.name = name;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
