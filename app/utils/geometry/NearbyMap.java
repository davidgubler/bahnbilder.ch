package utils.geometry;

import org.apache.pekko.japi.Pair;
import utils.Config;

import java.util.*;

public class NearbyMap<T> {
    private final double distanceKm;

    private Map<Integer, List<Pair<Point, T>>> latitudeMap;

    private final int subdivisions;

    public NearbyMap(double distanceKm) {
        this.distanceKm = distanceKm;
        double semicircleKm = GeographicCoordinates.R * Math.PI;
        subdivisions = (int)Math.floor(semicircleKm / distanceKm);
        latitudeMap = new HashMap<>();
    }

    private int bucket(double latitude) {
        return (int)(subdivisions * (latitude + 90.0) / 180.0);
    }

    public void put(Point point, T data) {
        int bucket = bucket(point.getLat());
        List<Pair<Point, T>> value = latitudeMap.get(bucket);
        if (value == null) {
            value = new LinkedList<>();
            value.add(Pair.create(point, data));
            latitudeMap.put(bucket, value);
        } else {
            value.add(Pair.create(point, data));
        }
    }

    public T getNearest(Point point) {
        int bucket = bucket(point.getLat());
        List<Pair<Point, T>> inRange = new ArrayList<>();
        List<Pair<Point, T>> value = latitudeMap.get(bucket);
        if (value != null) {
            inRange.addAll(value);
        }
        value = latitudeMap.get(bucket + 1);
        if (value != null) {
            inRange.addAll(value);
        }
        value = latitudeMap.get(bucket - 1);
        if (value != null) {
            inRange.addAll(value);
        }

        if (inRange.isEmpty()) {
            return null;
        }
        inRange.sort(Comparator.comparingDouble(p -> GeographicCoordinates.distanceKm(point, p.first())));
        if (GeographicCoordinates.distanceKm(point, inRange.get(0).first()) > Config.PHOTO_SPOT_RADIUS_KM) {
            return null;
        }
        return inRange.get(0).second();
    }

    public Set<Point> keySet() {
        Set<Point> keySet = new HashSet<>();
        latitudeMap.values().forEach(vl -> vl.forEach(v -> keySet.add(v.first())));
        return keySet;
    }

    public T get(Point point) {
        int bucket = bucket(point.getLat());
        return latitudeMap.get(bucket).stream().filter(e -> e.first().equals(point)).map(e -> e.second()).findFirst().orElse(null);
    }
}
