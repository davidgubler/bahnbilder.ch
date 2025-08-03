package entities.mongodb;
import dev.morphia.annotations.*;
import entities.*;
import org.bson.types.ObjectId;
import utils.Context;

import java.util.Objects;

@Entity(value = "vehicleClasses", useDiscriminator = false)
public class MongoDbVehicleClass implements MongoDbEntity, VehicleClass, ContextAwareEntity, Comparable<MongoDbVehicleClass> {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private String name;

    private String nameNumberFormat;

    private Integer vehicleSeriesId;

    private Integer vehicleTypeId;

    private Integer vehiclePropulsionId;

    @Transient
    private Context context;

    @Override
    public void inject(Context context) {
        this.context = context;
    }

    public MongoDbVehicleClass() {
        // dummy for Morphia
    }

    public MongoDbVehicleClass(int id, String name, String nameNumberFormat, Integer vehicleSeriesId, Integer vehicleTypeId, Integer vehiclePropulsionId) {
        this.numId = id;
        this.name = name;
        this.nameNumberFormat = nameNumberFormat;
        this.vehicleSeriesId = vehicleSeriesId;
        this.vehicleTypeId = vehicleTypeId;
        this.vehiclePropulsionId = vehiclePropulsionId;
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
    public String getNameNumberFormat() {
        return nameNumberFormat;
    }

    public void setNameNumberFormat(String nameNumberFormat) {
        this.nameNumberFormat = nameNumberFormat;
    }

    @Override
    public Integer getVehicleSeriesId() {
        return vehicleSeriesId;
    }

    public void setVehicleSeriesId(Integer vehicleSeriesId) {
        this.vehicleSeriesId = vehicleSeriesId;
    }

    @Transient
    private VehicleSeries vehicleSeries;

    @Override
    public VehicleSeries getVehicleSeries() {
        if (vehicleSeries == null) {
            vehicleSeries = context.getVehicleSeriesModel().get(vehicleSeriesId);
        }
        return vehicleSeries;
    }

    @Override
    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
        this.vehicleType = null;
    }

    @Transient
    private VehicleType vehicleType;

    @Override
    public VehicleType getVehicleType() {
        if (vehicleType == null) {
            vehicleType = context.getVehicleTypesModel().get(vehicleTypeId);
        }
        return vehicleType;
    }

    @Override
    public Integer getVehiclePropulsionId() {
        return vehiclePropulsionId;
    }

    public void setVehiclePropulsionId(Integer vehiclePropulsionId) {
        this.vehiclePropulsionId = vehiclePropulsionId;
        this.vehiclePropulsion = null;
    }

    @Transient
    private VehiclePropulsion vehiclePropulsion;

    @Override
    public VehiclePropulsion getVehiclePropulsion() {
        if (vehiclePropulsion == null) {
            vehiclePropulsion = context.getVehiclePropulsionsModel().get(vehiclePropulsionId);
        }
        return vehiclePropulsion;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(MongoDbVehicleClass mongoDbVehicleClass) {
        return getName().compareTo(mongoDbVehicleClass.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbVehicleClass that = (MongoDbVehicleClass) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_id);
    }
}
