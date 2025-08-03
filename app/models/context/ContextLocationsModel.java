package models.context;

import com.google.inject.Inject;
import entities.Location;
import models.LocationsModel;
import utils.Context;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class ContextLocationsModel extends ContextModel implements LocationsModel {

    @Inject
    private LocationsModel locationsModel;

    public ContextLocationsModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { locationsModel.clear(); return null; });
    }

    @Override
    public Location create(int numId, String name) {
        return call(() -> locationsModel.create(numId, name));
    }

    @Override
    public Location create(String name) {
        return call(() -> locationsModel.create(name));
    }

    @Override
    public Location get(Integer id) {
        return call(() -> locationsModel.get(id));
    }

    @Override
    public Location getByName(String name) {
        return call(() -> locationsModel.getByName(name));
    }

    @Override
    public Stream<? extends Location> getAll() {
        return call(() -> locationsModel.getAll());
    }

    @Override
    public Stream<? extends Location> get(Collection<Integer> ids) {
        return call(() -> locationsModel.get(ids));
    }

    @Override
    public Location getReverseLocation(Integer locationId) {
        return call(() -> locationsModel.getReverseLocation(locationId));
    }

    @Override
    public Map<Integer, ? extends Location> getByIdsAsMap(Collection<Integer> ids) {
        return call(() -> locationsModel.getByIdsAsMap(ids));
    }
}
