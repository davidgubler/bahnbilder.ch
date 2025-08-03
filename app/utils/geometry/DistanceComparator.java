package utils.geometry;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Point>  {
    private final Point from;

    public DistanceComparator(Point from) {
        this.from = from;
    }

    @Override
    public int compare(Point e1, Point e2) {
        return Double.compare(GeographicCoordinates.distanceKm(from, e1), GeographicCoordinates.distanceKm(from, e2));
    }
}
