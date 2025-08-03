package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.Views;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

@Entity(value = "views", useDiscriminator = false)
public class MongoDbViews implements MongoDbEntity, Views {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int photoId;

    private LocalDate date;

    private List<String> ips;

    public MongoDbViews() {
        // dummy for Morphia
    }

    public MongoDbViews(int photoId, LocalDate date, List<String> ips) {
        this.photoId = photoId;
        this.date = date;
        this.ips = ips;
    }
    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public List<String> getIps() {
        return ips;
    }

    @Override
    public int getPhotoId() {
        return photoId;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }
}