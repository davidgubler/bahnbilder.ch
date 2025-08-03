package utils.geometry;

public class GeographicCoordinates {
    public static final double R = 6371.0088;

    public static double bearingDegrees(Point point1, Point point2) {
        double lng1 = Math.toRadians(point1.getLng());
        double lat1 = Math.toRadians(point1.getLat());
        double lng2 = Math.toRadians(point2.getLng());
        double lat2 = Math.toRadians(point2.getLat());
        double lngDiff = lng2 - lng1;

        double X = Math.cos(lat2) * Math.sin(lngDiff);
        double Y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lngDiff);
        return (Math.toDegrees(Math.atan2(X,Y)) + 360) % 360;
    }

    public static double bearingDiff(double bearing1, double bearing2) {
        double t = Math.abs(bearing1 - bearing2) % 360;
        return t > 180 ? 360 - t : t;
    }

    public static double distanceKm(Point point1, Point point2) {
        Double latDistance = Math.toRadians(point2.getLat()-point1.getLat());
        Double lonDistance = Math.toRadians(point2.getLng()-point1.getLng());
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(point1.getLat())) * Math.cos(Math.toRadians(point2.getLat())) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public static SimplePoint goNorth(Point startingPoint, double distanceKm) {
        double newLat = startingPoint.getLat() + 180 * distanceKm / (Math.PI * R);
        return new SimplePoint.PointBuilder().withLat(newLat).withLng(startingPoint.getLng()).build();
    }

    public static SimplePoint goEast(Point startingPoint, double distanceKm) {
        double newLng = startingPoint.getLng() + 180 * distanceKm / (Math.PI * R * Math.cos(Math.toRadians(startingPoint.getLat())));
        return new SimplePoint.PointBuilder().withLat(startingPoint.getLat()).withLng(newLng).build();
    }
}
