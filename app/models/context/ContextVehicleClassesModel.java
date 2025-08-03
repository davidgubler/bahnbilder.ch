package models.context;

import com.google.inject.Inject;
import entities.VehicleClass;
import entities.formdata.VehicleClassFormData;
import models.VehicleClassesModel;
import utils.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ContextVehicleClassesModel extends ContextModel implements VehicleClassesModel {

    @Inject
    private VehicleClassesModel vehicleClassesModel;

    public ContextVehicleClassesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { vehicleClassesModel.clear(); return null; });
    }

    @Override
    public VehicleClass create(int id, String name, String nameNumberFormat, Integer vehicleSeriesId, Integer vehicleTypeId, Integer vehiclePropulsionId) {
        return call(() -> vehicleClassesModel.create(id, name, nameNumberFormat, vehicleSeriesId, vehicleTypeId, vehiclePropulsionId));
    }

    @Override
    public VehicleClass create(VehicleClassFormData data) {
        return call(() -> vehicleClassesModel.create(data));
    }

    @Override
    public void update(VehicleClassFormData data) {
        call(() -> { vehicleClassesModel.update(data); return null; });
    }

    private Map<Integer, VehicleClass> getIdCache = new HashMap<>();

    @Override
    public VehicleClass get(Integer id) {
        if (getIdCache.containsKey(id)) {
            return getIdCache.get(id);
        }
        VehicleClass vc = call(() -> vehicleClassesModel.get(id));
        getIdCache.put(id, vc);
        return vc;
    }

    @Override
    public VehicleClass getByName(String name) {
        return call(() -> vehicleClassesModel.getByName(name));
    }

    @Override
    public Stream<? extends VehicleClass> getAll() {
        return call(() -> vehicleClassesModel.getAll());
    }

    @Override
    public Stream<? extends VehicleClass> getByVehicleSeriesId(Integer vehicleSeriesId) {
        return call(() -> vehicleClassesModel.getByVehicleSeriesId(vehicleSeriesId));
    }

    @Override
    public Map<Integer, ? extends VehicleClass> getByIdsAsMap(Collection<Integer> ids) {
        return call(() -> vehicleClassesModel.getByIdsAsMap(ids));
    }

    @Override
    public Stream<? extends VehicleClass> getByIds(Collection<Integer> ids) {
        return call(() -> vehicleClassesModel.getByIds(ids));
    }

    @Override
    public Stream<? extends VehicleClass> getNotInIds(Collection<Integer> ids) {
        return call(() -> vehicleClassesModel.getNotInIds(ids));
    }

    @Override
    public Stream<? extends VehicleClass> getNoSeries() {
        return call(() -> vehicleClassesModel.getNoSeries());
    }

    @Override
    public Stream<? extends VehicleClass> getNoTypeProp() {
        return call(() -> vehicleClassesModel.getNoTypeProp());
    }
}
