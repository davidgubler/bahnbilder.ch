package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.mongodb.MongoDbRequestsDaily;
import entities.mongodb.MongoDbUrlStats;
import models.RequestsDailyModel;

import java.time.LocalDate;

public class MongoDbRequestsDailyModel extends MongoDbModel<MongoDbRequestsDaily> implements RequestsDailyModel {
    @Override
    public void track(String url, String referer) {
        LocalDate today = LocalDate.now();
        MongoDbRequestsDaily requestsDaily = query().filter(Filters.eq("date", today)).first();
        if (requestsDaily == null) {
            requestsDaily = new MongoDbRequestsDaily(today);
            try {
                mongoDb.getDs().save(requestsDaily);
            } catch (Exception e) {
                // may fail due to concurrent writes, it's fine
            }
            requestsDaily = query().filter(Filters.eq("date", today)).first();
        }

        MongoDbUrlStats stats = new MongoDbUrlStats(url, referer);

        if (requestsDaily.getUrlStats().containsKey(stats.getMapKey())) {
            query(requestsDaily).update(new UpdateOptions(), UpdateOperators.inc("urlStats." + stats.getMapKey() + ".cnt"));
        } else {
            query(requestsDaily).update(new UpdateOptions(), UpdateOperators.set("urlStats." + stats.getMapKey(), stats));
        }
    }
}
