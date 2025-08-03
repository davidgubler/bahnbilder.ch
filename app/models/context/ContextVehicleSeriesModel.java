package models.context;

import com.google.inject.Inject;
import entities.VehicleSeries;
import entities.formdata.VehicleSeriesFormData;
import models.VehicleSeriesModel;
import utils.Context;

import java.util.stream.Stream;

public class ContextVehicleSeriesModel extends ContextModel implements VehicleSeriesModel {

    @Inject
    private VehicleSeriesModel vehicleSeriesModel;

    public ContextVehicleSeriesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { vehicleSeriesModel.clear(); return null; });
    }

    @Override
    public VehicleSeries create(int id, String name) {
        return call(() -> vehicleSeriesModel.create(id, name));
    }

    @Override
    public VehicleSeries create(VehicleSeriesFormData data) {
        return call(() -> vehicleSeriesModel.create(data));
    }

    @Override
    public void update(VehicleSeriesFormData data) {
        call(() -> { vehicleSeriesModel.update(data); return null; });
    }

    @Override
    public VehicleSeries get(Integer id) {
        return call(() -> vehicleSeriesModel.get(id));
    }

    @Override
    public VehicleSeries getByName(String name) {
        return call(() -> vehicleSeriesModel.getByName(name));
    }

    @Override
    public Stream<? extends VehicleSeries> getAll() {
        return call(() -> vehicleSeriesModel.getAll());
    }
}
