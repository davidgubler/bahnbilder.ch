package models;

import entities.Location;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public interface LocationsModel {
    void clear();

    Location create(int numId, String name);

    Location create(String name);

    Location get(Integer id);

    Location getByName(String name);

    Stream<? extends Location> getAll();

    Stream<? extends Location> get(Collection<Integer> ids);

    Location getReverseLocation(Integer locationId);

    Map<Integer, ? extends Location> getByIdsAsMap(Collection<Integer> ids);
}
