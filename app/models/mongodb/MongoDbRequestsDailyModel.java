package models.mongodb;

import com.google.inject.Inject;
import com.mongodb.client.result.UpdateResult;
import dev.morphia.UpdateOptions;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import entities.RequestsDaily;
import entities.UrlStats;
import entities.mongodb.MongoDbRequestsDaily;
import entities.mongodb.MongoDbUrlStats;
import models.RequestsDailyModel;
import play.inject.ApplicationLifecycle;
import utils.BahnbilderLogger;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MongoDbRequestsDailyModel extends MongoDbModel<MongoDbRequestsDaily> implements RequestsDailyModel {

    private BahnbilderLogger logger = new BahnbilderLogger(MongoDbRequestsDailyModel.class);

    private ConcurrentLinkedQueue<UrlStats> trackingBuffer = new ConcurrentLinkedQueue();

    private static final int FLUSH_SIZE = 1000;

    private volatile boolean terminated = false;

    private CompletableFuture terminatingFuture = new CompletableFuture();

    private Thread flusher;

    @Inject
    private MongoDbRequestsDailyModel(ApplicationLifecycle appLifecycle) {
        super();
        appLifecycle.addStopHook(() -> {
            terminated = true;
            flusher.interrupt();
            return terminatingFuture;
        });
        flusher = new Thread(() -> {
            while (!terminated) {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    // the "stopped" flag should take care of it
                }
                do {
                    // Flush at least once. Repeat multiple times if there are too many items in the buffer.
                    List<UrlStats> flush = new ArrayList<>();
                    while (flush.size() < FLUSH_SIZE) {
                        UrlStats urlStats = trackingBuffer.poll();
                        if (urlStats == null) {
                            break;
                        }
                        flush.add(urlStats);
                    }
                    try {
                        track(flush);
                    } catch (Exception e) {
                        logger.error(null, e);
                    }
                } while (trackingBuffer.size() >= FLUSH_SIZE);
            }
            terminatingFuture.complete(null);
        });
        flusher.start();
    }

    private void track(Collection<UrlStats> urlStatsCollection) {
        if (urlStatsCollection.isEmpty() || !mongoDb.isWritable()) {
            // tracking requests isn't that important
            return;
        }
        LocalDate today = LocalDate.now();

        Map<String, MongoDbUrlStats> urlStatsMap = new HashMap<>();
        List<UpdateOperator> updateOperators = new ArrayList<>();

        for (UrlStats urlStats : urlStatsCollection) {
            urlStatsMap.put(urlStats.getMapKey(), (MongoDbUrlStats)urlStats);
            String setBase = "urlStats." + urlStats.getMapKey() + ".";
            updateOperators.add(UpdateOperators.inc(setBase + "c"));
            updateOperators.add(UpdateOperators.set(setBase + "u", urlStats.getUrl()));
            if (urlStats.getReferer() != null) {
                updateOperators.add(UpdateOperators.set(setBase + "r", urlStats.getReferer()));
            }
        }

        // The document with today's stats can be rather large hence we make sure we never actually load it from the server
        UpdateResult r = query().filter(Filters.eq("date", today)).update(new UpdateOptions(), updateOperators.toArray(new UpdateOperator[0]));
        if (r.getModifiedCount() < 1) {
            // The document for today doesn't seem to exist yet, create it
            try {
                mongoDb.getDs().save(new MongoDbRequestsDaily(today, urlStatsMap));
            } catch (Exception e) {
                // may fail due to concurrent writes, it's fine
            }
        }
    }

    @Override
    public void track(String url, String referer) {
        trackingBuffer.add(new MongoDbUrlStats(url, referer));
    }

    @Override
    public RequestsDaily getToday() {
        return query().filter(Filters.eq("date", LocalDate.now())).first();
    }

    @Override
    public List<? extends RequestsDaily> getRange(LocalDate from, LocalDate before) {
        return query().filter(Filters.gte("date", from), Filters.lt("date", before)).stream().toList();
    }

    @Override
    public LocalDate getFirstDate() {
        MongoDbRequestsDaily firstStats = query().stream(new FindOptions().projection().include("date").sort(Sort.ascending("date"))).findFirst().orElse(null);
        return firstStats == null ? null : firstStats.getDate();
    }
}
