package entities.mongodb;

import org.bson.types.ObjectId;

public interface MongoDbEntity {
    ObjectId getObjectId();

    default int getId() {
        return 0;
    }
}
