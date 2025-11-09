package services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.ReadPreference;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import play.inject.ApplicationLifecycle;
import com.google.inject.Inject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import entities.mongodb.*;
import utils.Config;

import java.util.concurrent.CompletableFuture;

public class MongoDb {
    private static final int TIMEOUT_CONNECT = 5 * 1000; // 5 seconds

    private static final int TIMEOUT_SOCKET = 5 * 1000; // 5 seconds

    private final static String dbName = "bahnbilder";

    private final static String filesOriginalSuffix = "-files-original";

    private final static String filesScaledSuffix = "-files-scaled";

    private final Datastore ds;

    private final Datastore dsFilesOriginal;

    private final Datastore dsFilesScaled;

    private final Datastore dsAdmin;

    private MongoClient client;

    @Inject
    public MongoDb(ApplicationLifecycle appLifecycle) {
        String hosts = Config.Option.MONGO_HOSTS.get() == null ? "localhost:27017" : Config.Option.MONGO_HOSTS.get();
        Boolean tls = "localhost:27017".equals(hosts) ? false : true;
        String username = Config.Option.MONGO_USERNAME.get();
        String password = Config.Option.MONGO_PASSWORD.get();
        String mongoUrl;
        if (username != null && password != null) {
            mongoUrl = "mongodb://" + username + ":" + password + "@" + hosts;
        } else {
            mongoUrl = "mongodb://" + hosts;
        }
        mongoUrl += "/?tls=" + tls.toString().toLowerCase() + "&connectTimeoutMS=" + TIMEOUT_CONNECT + "&socketTimeoutMS=" + TIMEOUT_SOCKET;

        MongoClientSettings settings = MongoClientSettings.builder()
                .readPreference(ReadPreference.primaryPreferred())
                .applyConnectionString(new ConnectionString(mongoUrl))
                .build();
        client = MongoClients.create(settings);

        MapperOptions mapperOptions = MapperOptions.builder().storeEmpties(false).storeNulls(false).build();
        dsAdmin = Morphia.createDatastore(client, "admin", mapperOptions);
        dsAdmin.getMapper().map(MongoDbReplSetStatus.class);

        MongoDbReplSetStatus replSetStatus = getReplSetStatus();
        System.out.println(replSetStatus == null ? "Replica set status not available" : replSetStatus);

        ds = Morphia.createDatastore(client, dbName, mapperOptions);
        ds.getMapper().map(MongoDbCalendarOrder.class);
        ds.getMapper().map(MongoDbCountry.class);
        ds.getMapper().map(MongoDbKeyword.class);
        ds.getMapper().map(MongoDbLocation.class);
        ds.getMapper().map(MongoDbOperator.class);
        ds.getMapper().map(MongoDbPhotoType.class);
        ds.getMapper().map(MongoDbTravelogue.class);
        ds.getMapper().map(MongoDbUser.class);
        ds.getMapper().map(MongoDbVehicleClass.class);
        ds.getMapper().map(MongoDbVehiclePropulsion.class);
        ds.getMapper().map(MongoDbVehicleSeries.class);
        ds.getMapper().map(MongoDbVehicleType.class);
        ds.getMapper().map(MongoDbViews.class);
        ds.getMapper().map(MongoDbWikidata.class);
        ds.getMapper().map(MongoDbPhoto.class);
        ds.getMapper().map(MongoDbRequestsDaily.class);

        if (isWritable()) {
            ds.ensureIndexes();
            ds.ensureCaps();
        }

        dsFilesOriginal = Morphia.createDatastore(client, dbName + filesOriginalSuffix, mapperOptions);
        dsFilesOriginal.getMapper().map(MongoDbFile.class);
        if (isWritable()) {
            dsFilesOriginal.ensureIndexes();
            dsFilesOriginal.ensureCaps();
        }

        dsFilesScaled = Morphia.createDatastore(client, dbName + filesScaledSuffix, mapperOptions);
        dsFilesScaled.getMapper().map(MongoDbFile.class);
        if (isWritable()) {
            dsFilesScaled.ensureIndexes();
            dsFilesScaled.ensureCaps();
        }

        appLifecycle.addStopHook(() -> {
            client.close();
            return CompletableFuture.completedFuture(null);
        });
    }

    public MongoDatabase get() {
        return ds.getDatabase();
    }

    public MongoDatabase getFilesOriginal() {
        return dsFilesOriginal.getDatabase();
    }

    public MongoDatabase getFilesScaled() {
        return dsFilesOriginal.getDatabase();
    }

    public Datastore getDs() {
        return ds;
    }

    public Datastore getDsFilesOriginal() {
        return dsFilesOriginal;
    }

    public Datastore getDsFilesScaled() {
        return dsFilesScaled;
    }

    public MongoDbReplSetStatus getReplSetStatus() {
        try {
            BsonDocument cmd = new BsonDocument();
            cmd.put("replSetGetStatus", new BsonInt32(1));
            return dsAdmin.getDatabase().runCommand(cmd, ReadPreference.primaryPreferred(), MongoDbReplSetStatus.class);
        } catch (MongoCommandException e) {
            // not a replica set or no permission to read the status
            return null;
        }
    }

    public boolean isWritable() {
        return client.getClusterDescription().hasWritableServer();
    }
}
