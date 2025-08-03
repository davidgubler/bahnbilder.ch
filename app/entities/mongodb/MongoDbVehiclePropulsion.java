package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.VehiclePropulsion;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Entity(value = "vehiclePropulsions", useDiscriminator = false)
public class MongoDbVehiclePropulsion implements MongoDbEntity, VehiclePropulsion {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private Map<String, String> names = new HashMap<>();

    public MongoDbVehiclePropulsion() {
        // dummy for Morphia
    }

    public MongoDbVehiclePropulsion(int id, String nameDe, String nameEn) {
        this.numId = id;
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
    public String getName(String lang) {
        return names.get(lang);
    }

    public void setName(String lang, String name) {
        this.names.put(lang, name);
    }
}
