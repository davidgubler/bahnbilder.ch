package entities.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.morphia.annotations.*;
import entities.Location;
import org.bson.types.ObjectId;

import static dev.morphia.utils.IndexType.TEXT;

@Entity(value = "locations", useDiscriminator = false)
@Indexes({
        @Index(fields = @Field(value = "name", type = TEXT)),
        @Index(fields = @Field(value = "numId"), options =  @IndexOptions(unique = true)),
        @Index(fields = @Field(value = "name"), options =  @IndexOptions(unique = true)),
})
public class MongoDbLocation implements MongoDbEntity, Location {
    @JsonIgnore
    @Id
    private ObjectId _id;

    private int numId;

    private String name;

    private Float searchScore;

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

    public Float getSearchScore() {
        return searchScore;
    }
}
