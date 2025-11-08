package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import entities.RequestsDaily;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity(value = "requestsDaily", useDiscriminator = false)
public class MongoDbRequestsDaily implements RequestsDaily, MongoDbEntity {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private LocalDate date;

    private Map<String, MongoDbUrlStats> urlStats = new HashMap<>();

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    public MongoDbRequestsDaily() {
        // dummy for Morphia
    }

    public MongoDbRequestsDaily(LocalDate date) {
        this.date = date;
    }

    @Override
    public Map<String, MongoDbUrlStats> getUrlStats() {
        return urlStats;
    }
}
