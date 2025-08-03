package utils.geometry;

public class SimplePoint implements Point {
    private double latitude;
    private double longitude;

    // lat, lng
    public static SimplePoint fromString(String coordinates) {
        if (coordinates == null || coordinates.isBlank()) {
            return null;
        }
        coordinates = coordinates.trim();
        String[] split = coordinates.split(",");
        if (split.length != 2) {
            split = coordinates.split(";");
        }
        if (split.length != 2) {
            split = coordinates.split("\\s");
        }
        if (split.length != 2) {
            return null;
        }

        try {
            Double lat = Double.parseDouble(split[0].trim());
            Double lng = Double.parseDouble(split[1].trim());
            return new SimplePoint(lat, lng);
        } catch (Exception e) {
            return null;
        }
    }

    public SimplePoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLat() {
        return latitude;
    }

    public double getLng() {
        return longitude;
    }

    // we don't want the users to use the Point constructor directly to avoid mixing up latitude and longitude
    public static class PointBuilder {
        private double latitude;
        private double longitude;
        public PointBuilder withLat(double latitude) {
            this.latitude = latitude;
            return this;
        }
        public PointBuilder withLng(double longitude) {
            this.longitude = longitude;
            return this;
        }
        public SimplePoint build() {
            return new SimplePoint(latitude, longitude);
        }
    }
}
