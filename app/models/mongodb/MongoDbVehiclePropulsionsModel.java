package models.mongodb;

import entities.VehiclePropulsion;
import entities.mongodb.MongoDbVehiclePropulsion;
import models.VehiclePropulsionsModel;

public class MongoDbVehiclePropulsionsModel extends MongoDbModel<MongoDbVehiclePropulsion> implements VehiclePropulsionsModel {
    @Override
    public VehiclePropulsion create(int id, String nameDe, String nameEn) {
        VehiclePropulsion vehiclePropulsion = new MongoDbVehiclePropulsion(id, nameDe, nameEn);
        mongoDb.getDs().save(vehiclePropulsion);
        return vehiclePropulsion;
    }
}
