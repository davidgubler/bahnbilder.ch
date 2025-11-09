package entities;

import entities.mongodb.MongoDbUrlStats;

import java.util.Map;

public interface RequestsDaily {
    Map<String, MongoDbUrlStats> getUrlStats();
}
