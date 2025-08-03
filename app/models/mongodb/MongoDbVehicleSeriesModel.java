package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.VehicleSeries;
import entities.formdata.VehicleSeriesFormData;
import entities.mongodb.MongoDbVehicleSeries;
import models.VehicleSeriesModel;

public class MongoDbVehicleSeriesModel extends MongoDbModel<MongoDbVehicleSeries> implements VehicleSeriesModel {
    @Override
    public VehicleSeries create(int id, String name) {
        VehicleSeries vehicleSeries = new MongoDbVehicleSeries(id, name);
        mongoDb.getDs().save(vehicleSeries);
        return vehicleSeries;
    }

    @Override
    public VehicleSeries create(VehicleSeriesFormData data) {
        VehicleSeries vehicleSeries = null;
        for (int i = 0; i < 10; i++) {
            try {
                vehicleSeries = new MongoDbVehicleSeries(getNextNumId(), data.name);
                mongoDb.getDs().save(vehicleSeries);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return vehicleSeries;
    }

    @Override
    public void update(VehicleSeriesFormData data) {
        MongoDbVehicleSeries mongoDbVehicleSeries = (MongoDbVehicleSeries)data.entity;
        mongoDbVehicleSeries.setName(data.name);
        query(mongoDbVehicleSeries).update(new UpdateOptions(), UpdateOperators.set("name", mongoDbVehicleSeries.getName()));
    }

    @Override
    public VehicleSeries getByName(String name) {
        return query().filter(Filters.eq("name", name)).first();
    }
}
