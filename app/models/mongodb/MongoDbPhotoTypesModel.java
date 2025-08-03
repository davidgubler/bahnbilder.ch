package models.mongodb;

import entities.PhotoType;
import entities.mongodb.MongoDbPhotoType;
import models.PhotoTypesModel;

public class MongoDbPhotoTypesModel extends MongoDbModel<MongoDbPhotoType> implements PhotoTypesModel {
    @Override
    public PhotoType create(int id, String nameDe, String nameEn) {
        PhotoType photoType = new MongoDbPhotoType(id, nameDe, nameEn);
        mongoDb.getDs().save(photoType);
        return photoType;
    }
}
