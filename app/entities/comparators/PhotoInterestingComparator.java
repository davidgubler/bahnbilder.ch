package entities.comparators;

import entities.Photo;
import entities.VehicleClass;
import entities.VehicleType;
import models.PhotosModel;
import models.VehicleClassesModel;
import models.VehicleTypesModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PhotoInterestingComparator implements Comparator<Photo> {

    private final VehicleClassesModel vehicleClassesModel;

    private final VehicleTypesModel vehicleTypesModel;

    private final PhotosModel photosModel;

    public PhotoInterestingComparator(PhotosModel photosModel, VehicleClassesModel vehicleClassesModel, VehicleTypesModel vehicleTypesModel) {
        this.vehicleClassesModel = vehicleClassesModel;
        this.vehicleTypesModel = vehicleTypesModel;
        this.photosModel = photosModel;
    }

    private Map<Integer, Integer> vehicleClassOrderMap = new HashMap<>();

    private int vehicleClassOrder(Integer vehicleClassId) {
        if (vehicleClassId == null) {
            return Integer.MAX_VALUE;
        }

        if (vehicleClassOrderMap.containsKey(vehicleClassId)) {
            return vehicleClassOrderMap.get(vehicleClassId);
        }

        VehicleClass vehicleClass = vehicleClassesModel.get(vehicleClassId);
        if (vehicleClass != null) {
            VehicleType vehicleType = vehicleTypesModel.get(vehicleClass.getVehicleTypeId());
            if (vehicleType != null) {
                vehicleClassOrderMap.put(vehicleClassId, vehicleType.getOrder());
                return vehicleType.getOrder();
            }
        }

        return Integer.MAX_VALUE;
    }


    private Map<Integer, Integer> locationCardinalityMap = new HashMap<>();

    private int locationCardinality(Integer locationId) {
        if (locationId == null) {
            return Integer.MAX_VALUE;
        }
        Integer locationCardinality = locationCardinalityMap.get(locationId);
        if (locationCardinality == null) {
            locationCardinalityMap.put(locationId, photosModel.getLocationCardinality(locationId));
        }
        locationCardinality = locationCardinalityMap.get(locationId);
        if (locationCardinality == 0) {
            return Integer.MAX_VALUE; // some race condition
        }
        return locationCardinality;
    }

    private Map<Integer, Integer> vehicleClassCardinalityMap = new HashMap<>();

    private int vehicleClassCardinality(Integer vehicleClassId) {
        if (vehicleClassId == null) {
            return Integer.MAX_VALUE;
        }
        Integer vehicleClassCardinality = vehicleClassCardinalityMap.get(vehicleClassId);
        if (vehicleClassCardinality == null) {
            vehicleClassCardinalityMap.put(vehicleClassId, photosModel.getVehicleClassCardinality(vehicleClassId));
        }
        vehicleClassCardinality = vehicleClassCardinalityMap.get(vehicleClassId);
        if (vehicleClassCardinality == 0) {
            return Integer.MAX_VALUE; // some race condition
        }
        return vehicleClassCardinality;
    }

    @Override
    public int compare(Photo photo1, Photo photo2) {
        // check photo type, return lower order
        if (vehicleClassOrder(photo1.getVehicleClassId()) != vehicleClassOrder(photo2.getVehicleClassId())) {
            return Integer.compare(vehicleClassOrder(photo1.getVehicleClassId()), vehicleClassOrder(photo2.getVehicleClassId()));
        }

        long c1 = vehicleClassCardinality(photo1.getVehicleClassId()) * locationCardinality(photo1.getLocationId());
        long c2 = vehicleClassCardinality(photo2.getVehicleClassId()) * locationCardinality(photo2.getLocationId());

        return Long.compare(c1, c2);
    }
}
