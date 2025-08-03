package entities;

public interface VehicleClass extends LocalizedEntity {
    int getId();

    String getName();

    default String getName(String lang) {
        return getName();
    }

    String getNameNumberFormat();

    Integer getVehicleSeriesId();

    VehicleSeries getVehicleSeries();

    Integer getVehicleTypeId();

    VehicleType getVehicleType();

    Integer getVehiclePropulsionId();

    VehiclePropulsion getVehiclePropulsion();
}
