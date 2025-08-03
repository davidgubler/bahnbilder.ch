package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.VehicleClass;
import entities.formdata.VehicleClassFormData;
import entities.mongodb.MongoDbVehicleClass;
import models.VehicleClassesModel;

import java.util.Collection;
import java.util.stream.Stream;

public class MongoDbVehicleClassesModel extends MongoDbModel<MongoDbVehicleClass> implements VehicleClassesModel {
    @Override
    public VehicleClass create(int id, String name, String nameNumberFormat, Integer vehicleSeriesId, Integer vehicleTypeId, Integer vehiclePropulsionId) {
        VehicleClass vehicleClass = new MongoDbVehicleClass(id, name, nameNumberFormat, vehicleSeriesId, vehicleTypeId, vehiclePropulsionId);
        mongoDb.getDs().save(vehicleClass);
        return vehicleClass;
    }

    @Override
    public VehicleClass create(VehicleClassFormData data) {
        VehicleClass vehicleClass = null;
        for (int i = 0; i < 10; i++) {
            try {
                vehicleClass = new MongoDbVehicleClass(getNextNumId(), data.name, data.nameNumberFormat, data.vehicleSeriesId, data.vehicleTypeId, data.vehiclePropulsionId);
                mongoDb.getDs().save(vehicleClass);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return vehicleClass;
    }

    @Override
    public void update(VehicleClassFormData data) {
        MongoDbVehicleClass mongoDbVehicleClass = (MongoDbVehicleClass)data.entity;
        mongoDbVehicleClass.setName(data.name);
        mongoDbVehicleClass.setNameNumberFormat(data.nameNumberFormat);
        mongoDbVehicleClass.setVehicleSeriesId(data.vehicleSeriesId);
        mongoDbVehicleClass.setVehicleTypeId(data.vehicleTypeId);
        mongoDbVehicleClass.setVehiclePropulsionId(data.vehiclePropulsionId);
        query(mongoDbVehicleClass).update(new UpdateOptions(),
                UpdateOperators.set("name", mongoDbVehicleClass.getName()),
                UpdateOperators.set("nameNumberFormat", mongoDbVehicleClass.getNameNumberFormat()),
                UpdateOperators.set("vehicleSeriesId", mongoDbVehicleClass.getVehicleSeriesId()),
                UpdateOperators.set("vehicleTypeId", mongoDbVehicleClass.getVehicleTypeId()),
                UpdateOperators.set("vehiclePropulsionId", mongoDbVehicleClass.getVehiclePropulsionId()));
    }

    @Override
    public Stream<? extends VehicleClass> getByVehicleSeriesId(Integer vehicleSeriesId) {
        if (vehicleSeriesId == null) {
            return Stream.empty();
        }
        return query().filter(Filters.eq("vehicleSeriesId", vehicleSeriesId)).stream();
    }

    @Override
    public VehicleClass getByName(String name) {
        return query().filter(Filters.eq("name", name)).first();
    }

    @Override
    public Stream<MongoDbVehicleClass> getNotInIds(Collection<Integer> ids) {
        return query().filter(Filters.nin("numId", ids)).stream();
    }

    @Override
    public Stream<? extends VehicleClass> getNoSeries() {
        return query().filter(Filters.eq("vehicleSeriesId", null)).stream();
    }

    @Override
    public Stream<? extends VehicleClass> getNoTypeProp() {
        return query().filter(Filters.or(Filters.eq("vehicleTypeId", null), (Filters.eq("vehiclePropulsionId", null)))).stream();
    }
}
