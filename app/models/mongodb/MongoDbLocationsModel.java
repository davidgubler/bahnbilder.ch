package models.mongodb;

import dev.morphia.query.filters.Filters;
import entities.Location;
import entities.mongodb.MongoDbLocation;
import models.LocationsModel;

import java.util.Collection;
import java.util.stream.Stream;

public class MongoDbLocationsModel extends MongoDbModel<MongoDbLocation> implements LocationsModel {

    public final String SEPARATOR = " - ";

    @Override
    public Location create(int numId, String name) {
        Location location = new MongoDbLocation(numId, name);
        mongoDb.getDs().save(location);
        return location;
    }

    @Override
    public Location create(String name) {
        Location location = null;
        for (int i = 0; i < 10; i++) {
            try {
                location = new MongoDbLocation(getNextNumId(), name);
                mongoDb.getDs().save(location);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return location;
    }

    @Override
    public Stream<? extends Location> get(Collection<Integer> ids) {
        return query().filter(Filters.in("numId", ids)).stream();
    }

    @Override
    public Location getByName(String name) {
        return query().filter(Filters.eq("name", name)).stream().findFirst().orElse(null);
    }

    @Override
    public Location getReverseLocation(Integer locationId) {
        Location location = get(locationId);
        if (location == null) {
            return null;
        }
        int pos = location.getName().indexOf(SEPARATOR);
        if (pos < 0) {
            return null;
        }
        String reverseName = location.getName().substring(pos + SEPARATOR.length()) + SEPARATOR + location.getName().substring(0, pos);
        return getByName(reverseName);
    }
}
