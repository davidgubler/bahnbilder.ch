package models.context;

import com.google.inject.Inject;
import entities.VehiclePropulsion;
import models.VehiclePropulsionsModel;
import utils.Context;

import java.util.stream.Stream;

public class ContextVehiclePropulsionsModel extends ContextModel implements VehiclePropulsionsModel {

    @Inject
    private VehiclePropulsionsModel vehiclePropulsionsModel;

    public ContextVehiclePropulsionsModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { vehiclePropulsionsModel.clear(); return null; });
    }

    @Override
    public VehiclePropulsion create(int id, String nameDe, String nameEn) {
        return call(() -> vehiclePropulsionsModel.create(id, nameDe, nameEn));
    }

    @Override
    public VehiclePropulsion get(Integer id) {
        return call(() -> vehiclePropulsionsModel.get(id));
    }

    @Override
    public Stream<? extends VehiclePropulsion> getAll() {
        return call(() -> vehiclePropulsionsModel.getAll());
    }
}
