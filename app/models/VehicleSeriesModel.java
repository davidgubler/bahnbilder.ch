package models;

import entities.VehicleSeries;
import entities.formdata.VehicleSeriesFormData;

import java.util.stream.Stream;

public interface VehicleSeriesModel {
    void clear();

    VehicleSeries create(int id, String name);

    VehicleSeries create(VehicleSeriesFormData data);

    void update(VehicleSeriesFormData data);

    VehicleSeries get(Integer id);

    VehicleSeries getByName(String name);

    Stream<? extends VehicleSeries> getAll();
}
