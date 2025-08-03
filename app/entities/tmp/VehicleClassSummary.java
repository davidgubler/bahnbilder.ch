package entities.tmp;

import entities.Photo;
import entities.Search;
import entities.VehicleClass;
import entities.VehicleSeries;

import java.util.List;

public class VehicleClassSummary {
    private final Photo photo;
    private final Search search;
    private final long searchCount;
    private final VehicleClass vehicleClass;
    private final VehicleSeries vehicleSeries;
    private final List<Integer> nrs;

    public VehicleClassSummary(Photo photo, Search search, long searchCount, VehicleClass vehicleClass, VehicleSeries vehicleSeries, List<Integer> nrs) {
        this.photo = photo;
        this.search = search;
        this.searchCount = searchCount;
        this.vehicleClass = vehicleClass;
        this.vehicleSeries = vehicleSeries;
        this.nrs = nrs;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Search getSearch() {
        return search;
    }

    public long getSearchCount() {
        return searchCount;
    }

    public VehicleClass getVehicleClass() {
        return vehicleClass;
    }

    public VehicleSeries getVehicleSeries() {
        return vehicleSeries;
    }

    public List<Integer> getNrs() {
        return nrs;
    }
}
