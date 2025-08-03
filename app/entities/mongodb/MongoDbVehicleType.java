package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.VehicleType;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity(value = "vehicleTypes", useDiscriminator = false)
public class MongoDbVehicleType implements MongoDbEntity, VehicleType {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private Map<String, String> names = new HashMap<>();

    private Map<String, String> plurals = new HashMap<>();

    private int order;

    public MongoDbVehicleType() {
        // dummy for Morphia
    }

    public MongoDbVehicleType(int id, String nameDe, String nameEn, String pluralDe, String pluralEn, int order) {
        this.numId = id;
        names.put("de", nameDe);
        names.put("en", nameEn);
        plurals.put("de", pluralDe);
        plurals.put("en", pluralEn);
        this.order = order;
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

    @Override
    public String getPlural(String lang) {
        return plurals.get(lang);
    }

    public void setPlural(String lang, String name) {
        this.plurals.put(lang, name);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbVehicleType that = (MongoDbVehicleType) o;
        return numId == that.numId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numId);
    }
}
