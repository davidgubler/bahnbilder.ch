package models.mongodb;

import com.mongodb.client.result.UpdateResult;
import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import entities.mongodb.MongoDbRequestsDaily;
import entities.mongodb.MongoDbUrlStats;
import models.RequestsDailyModel;

import java.time.LocalDate;
import java.util.Map;

public class MongoDbRequestsDailyModel extends MongoDbModel<MongoDbRequestsDaily> implements RequestsDailyModel {
    @Override
    public void track(String url, String ref) {
        if (!mongoDb.isWritable()) {
            // tracking requests isn't that important
            return;
        }
        LocalDate today = LocalDate.now();
        MongoDbUrlStats stats = new MongoDbUrlStats(url, ref);
        String setBase = "urlStats." + stats.getMapKey() + ".";

        UpdateOperator[] ops = new UpdateOperator[ref == null ? 2 : 3];
        ops[0] = UpdateOperators.inc(setBase + "cnt");
        ops[1] = UpdateOperators.set(setBase + "url", url);
        if (ref != null) {
            ops[2] = UpdateOperators.set(setBase + "ref", ref);
        }

        // The document with today's stats can be rather large hence we make sure we never actually load it from the server
        UpdateResult r = query().filter(Filters.eq("date", today)).update(new UpdateOptions(), ops);
        if (r.getModifiedCount() < 1) {
            // The document for today doesn't seem to exist yet, create it
            try {
                mongoDb.getDs().save(new MongoDbRequestsDaily(today, Map.of(stats.getMapKey(), stats)));
            } catch (Exception e) {
                // may fail due to concurrent writes, it's fine
            }
        }
    }
}
