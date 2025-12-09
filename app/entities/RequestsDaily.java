package entities;

import entities.mongodb.MongoDbUrlStats;

import java.time.LocalDate;
import java.util.Map;

public interface RequestsDaily {
    Map<String, MongoDbUrlStats> getUrlStats();

    LocalDate getDate();
}
