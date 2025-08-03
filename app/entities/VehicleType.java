package entities;

public interface VehicleType extends LocalizedEntity, Comparable<VehicleType> {
    int getId();

    String getName(String lang);

    String getPlural(String lang);

    int getOrder();

    @Override
    default int compareTo(VehicleType vehicleType) {
        return Integer.compare(getOrder(), vehicleType.getOrder());
    }
}
