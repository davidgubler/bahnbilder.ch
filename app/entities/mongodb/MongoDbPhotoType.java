package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.PhotoType;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Entity(value = "photoTypes", useDiscriminator = false)
public class MongoDbPhotoType implements MongoDbEntity, PhotoType {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private Map<String, String> name = new HashMap<>();

    public MongoDbPhotoType() {
        // dummy for Morphia
    }

    public MongoDbPhotoType(int id, String nameDe, String nameEn) {
        this.numId = id;
        this.name.put("de", nameDe);
        this.name.put("en", nameEn);
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public int getId() {
        return numId;
    }

    @Override
    public String getName(String lang) {
        return name.get(lang);
    }

    public void setName(String lang, String name) {
        this.name.put(lang, name);
    }
}
