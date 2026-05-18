package biz;

import entities.*;
import play.libs.F;
import utils.Context;

import java.util.*;

public class FreeTextSearch {

    public static List<String> tokenize(String freeText) {
        List<String> tokens = new ArrayList<>();
        boolean quoted = false;
        for( String s : freeText.split("\"") ) {
            if (quoted) {
                tokens.add(s);
            } else {
                tokens.addAll(Arrays.asList(s.split(" ")));
            }
            quoted = !quoted;
        }
        return tokens.stream().map(t -> t.trim()).filter(t -> !t.isEmpty()).toList();
    }

    // FIXME: Missing search by number, description, keywords, photographer, detected text, detected objects

    public static class TokenResult {
        private final String token;
        private final Map<? extends User, Float> users;
        private final Map<? extends Country, Float> countries;
        private final Map<? extends Location, Float> locations;
        private final Map<? extends Operator, Float> operators;
        private final Map<? extends VehicleClass, Float> vehicleClasses;
        private final Map<? extends VehicleClass, Float> vehicleClassesBySeries;

        public TokenResult(String token,
                            Map<? extends User, Float> users,
                            Map<? extends Country, Float> countries,
                            Map<? extends Location, Float> locations,
                            Map<? extends Operator, Float> operators,
                            Map<? extends VehicleClass, Float> vehicleClasses,
                            Map<? extends VehicleClass, Float> vehicleClassesBySeries) {
            this.token = token;
            this.users = users;
            this.countries = countries;
            this.locations = locations;
            this.operators = operators;
            this.vehicleClasses = vehicleClasses;
            this.vehicleClassesBySeries = vehicleClassesBySeries;
        }

        @Override
        public String toString() {
            String s = token;
            if (!users.isEmpty()) {
                s += "->" + users;
            }
            if (!countries.isEmpty()) {
                s += "->" + countries;
            }
            if (!locations.isEmpty()) {
                s += "->" + locations;
            }
            if (!operators.isEmpty()) {
                s += "->" + operators;
            }
            if (!vehicleClasses.isEmpty()) {
                s += "->" + vehicleClasses;
            }
            if (!vehicleClassesBySeries.isEmpty()) {
                s += "->" + vehicleClassesBySeries;
            }
            return s;
        }

        public Map<? extends User, Float> getUsers() {
            return users;
        }

        public Map<? extends Country, Float> getCountries() {
            return countries;
        }

        public Map<? extends Location, Float> getLocations() {
            return locations;
        }

        public Map<? extends Operator, Float> getOperators() {
            return operators;
        }

        public Map<? extends VehicleClass, Float> getVehicleClasses() {
            return vehicleClasses;
        }

        public Map<? extends VehicleClass, Float> getVehicleClassesBySeries() {
            return vehicleClassesBySeries;
        }
    }

    private static float rank(List<TokenResult> tokenResults, Photo photo) {
        float[] points = new float[1]; // the lambdas want an immutable variable, hence this array hack
        points[0] = 0.0f;

        for (TokenResult r : tokenResults) {
            r.getUsers().keySet().stream().filter(u -> Objects.equals(u.getId(), photo.getUserId())).forEach(u -> points[0] += r.getUsers().get(u));
            r.getCountries().keySet().stream().filter(c -> Objects.equals(c.getId(), photo.getCountryId())).forEach(c -> points[0] += r.getCountries().get(c));
            r.getLocations().keySet().stream().filter(l -> Objects.equals(l.getId(), photo.getLocationId())).forEach(l -> points[0] += r.getLocations().get(l));
            r.getOperators().keySet().stream().filter(o -> Objects.equals(o.getId(), photo.getOperatorId())).forEach(o -> points[0] += r.getOperators().get(o));
            r.getVehicleClasses().keySet().stream().filter(v -> Objects.equals(v.getId(), photo.getVehicleClassId())).forEach(v -> points[0] += r.getVehicleClasses().get(v));
            r.getVehicleClassesBySeries().keySet().stream().filter(v -> Objects.equals(v.getId(), photo.getVehicleClassId())).forEach(v -> points[0] += r.getVehicleClassesBySeries().get(v));
        }

        return points[0];
    }

    public static List<? extends Photo> search(Context context, ContextSearch search) {
        List<? extends Photo> photos = new ArrayList<>(context.getPhotosModel().searchAll(search).toList());
        Collections.sort(photos, new PhotoRankComparator(search.getFreeTextSearchTokenResults()));
        return photos;
    }

    private static class PhotoRankComparator implements Comparator<Photo> {
        private final List<TokenResult> tokenResults;

        public PhotoRankComparator(List<TokenResult> tokenResults) {
            this.tokenResults = tokenResults;
        }

        @Override
        public int compare(Photo p1, Photo p2) {
            float r1 = rank(tokenResults, p1);
            float r2 = rank(tokenResults, p2);
            if (r1 != r2) {
                return r1 < r2 ? 1 : -1;
            }
            if (p1.getAuthorRating() != p2.getAuthorRating()) {
                return p1.getAuthorRating() < p2.getAuthorRating() ? 1 : -1;
            }
            if (p1.getViews() != p2.getViews()) {
                return p1.getViews() < p2.getViews() ? 1 : -1;
            }
            return p1.getId() < p2.getId() ? 1 : -1;
        }
    }

    public static F.Tuple<Photo, Photo> getPrevNext(Context context, Photo photo, ContextSearch search) {
        List<? extends Photo> photos = search(context, search);
        int pos = photos.indexOf(photo);
        if (pos == -1) {
            return new F.Tuple<>(null, null);
        }
        Photo prev = null;
        Photo next = null;
        if (pos > 0) {
            prev = photos.get(pos - 1);
        }
        if (pos < photos.size() - 1) {
            next = photos.get(pos + 1);
        }
        return new F.Tuple<>(prev, next);
    }
}
