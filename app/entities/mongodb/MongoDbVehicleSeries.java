package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.VehicleSeries;
import org.bson.types.ObjectId;

@Entity(value = "vehicleSeries", useDiscriminator = false)
public class MongoDbVehicleSeries implements MongoDbEntity, VehicleSeries {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    @Indexed(options = @IndexOptions(unique = true))
    private String name;

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
}
