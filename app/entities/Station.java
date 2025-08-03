package entities;

import utils.geometry.Point;
import utils.geometry.SimplePoint;

import java.util.Objects;

public class Station implements Point {
    private final String name;
    private final SimplePoint location;

    public Station(String name, SimplePoint location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    @Override
    public double getLat() {
        return location.getLat();
    }

    @Override
    public double getLng() {
        return location.getLng();
    }

    @Override
    public String toString() {
        return name;
    }

    public SimplePoint getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
