package models.context;

import com.google.inject.Inject;
import entities.VehicleType;
import models.VehicleTypesModel;
import utils.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ContextVehicleTypesModel extends ContextModel implements VehicleTypesModel {

    @Inject
    private VehicleTypesModel vehicleTypesModel;

    public ContextVehicleTypesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { vehicleTypesModel.clear(); return null; });
    }

    @Override
    public VehicleType create(int id, String nameDe, String nameEn, String pluralDe, String pluralEn, int order) {
        return call(() -> vehicleTypesModel.create(id, nameDe, nameEn, pluralDe, pluralEn, order));
    }

    private Map<Integer, VehicleType> getIdCache = new HashMap<>();

    @Override
    public VehicleType get(Integer id) {
        if (getIdCache.containsKey(id)) {
            return getIdCache.get(id);
        }
        VehicleType vc = call(() -> vehicleTypesModel.get(id));
        getIdCache.put(id, vc);
        return vc;
    }

    @Override
    public Stream<? extends VehicleType> getAll() {
        return call(() -> vehicleTypesModel.getAll());
    }

    @Override
    public Stream<? extends VehicleType> getByIds(Collection<Integer> ids) {
        return call(() -> vehicleTypesModel.getByIds(ids));
    }
}
