package entities.mongodb;

import dev.morphia.annotations.*;
import entities.VehicleSeries;
import org.bson.types.ObjectId;

import static dev.morphia.utils.IndexType.TEXT;

@Entity(value = "vehicleSeries", useDiscriminator = false)
@Indexes({
        @Index(fields = @Field(value = "numId"), options = @IndexOptions(unique = true)),
        @Index(fields = @Field(value = "name"), options = @IndexOptions(unique = true)),
        @Index(fields = @Field(value = "name", type = TEXT)),
})
public class MongoDbVehicleSeries implements MongoDbEntity, VehicleSeries {
    @Id
    private ObjectId _id;

    private int numId;

    private String name;

    private Float searchScore;

    public MongoDbVehicleSeries() {
        // dummy for Morphia
    }

    public MongoDbVehicleSeries(int id, String name) {
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
