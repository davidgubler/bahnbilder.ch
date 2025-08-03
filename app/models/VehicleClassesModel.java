package models;

import entities.VehicleClass;
import entities.formdata.VehicleClassFormData;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public interface VehicleClassesModel {
    void clear();

    VehicleClass create(int id, String name, String nameNumberFormat, Integer vehicleSeriesId, Integer vehicleTypeId, Integer vehiclePropulsionId);

    VehicleClass create(VehicleClassFormData data);

    void update(VehicleClassFormData data);

    VehicleClass get(Integer id);

    VehicleClass getByName(String name);

    Stream<? extends VehicleClass> getAll();

    Stream<? extends VehicleClass> getByVehicleSeriesId(Integer vehicleSeriesId);

    Map<Integer, ? extends VehicleClass> getByIdsAsMap(Collection<Integer> ids);

    Stream<? extends VehicleClass> getByIds(Collection<Integer> ids);

    Stream<? extends VehicleClass> getNotInIds(Collection<Integer> ids);

    Stream<? extends VehicleClass> getNoSeries();

    Stream<? extends VehicleClass> getNoTypeProp();
}
