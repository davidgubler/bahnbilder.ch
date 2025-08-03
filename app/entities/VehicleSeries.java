package entities;

public interface VehicleSeries extends LocalizedEntity {
    int getId();

    String getName();

    default String getName(String lang) {
        return getName();
    }
}
