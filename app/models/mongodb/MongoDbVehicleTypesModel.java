package models.mongodb;

import entities.VehicleType;
import entities.mongodb.MongoDbVehicleType;
import models.VehicleTypesModel;

public class MongoDbVehicleTypesModel extends MongoDbModel<MongoDbVehicleType> implements VehicleTypesModel {
    @Override
    public VehicleType create(int id, String nameDe, String nameEn, String pluralDe, String pluralEn, int order) {
        VehicleType vehicleType = new MongoDbVehicleType(id, nameDe, nameEn, pluralDe, pluralEn, order);
        mongoDb.getDs().save(vehicleType);
        return vehicleType;
    }
}
