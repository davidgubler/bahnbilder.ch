package biz;

import entities.*;
import utils.Context;

import java.util.*;

public class FreeTextSearch {

    private List<String> tokenize(String freeText) {
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

    // FIXME: Missing search by family, number, description, keywords, photographer, detected text, detected objects

    public static class TokenResult {
        String token;
        Map<? extends User, Float> users;
        Map<? extends Country, Float> countries;
        Map<? extends Location, Float> locations;
        Map<? extends Operator, Float> operators;
        Map<? extends VehicleClass, Float> vehicleClasses;

        public TokenResult(String token,
                            Map<? extends User, Float> users,
                            Map<? extends Country, Float> countries,
                            Map<? extends Location, Float> locations,
                            Map<? extends Operator, Float> operators,
                            Map<? extends VehicleClass, Float> vehicleClasses) {
            this.token = token;
            this.users = users;
            this.countries = countries;
            this.locations = locations;
            this.operators = operators;
            this.vehicleClasses = vehicleClasses;
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
    }

    private float rank(List<TokenResult> tokenResults, Photo photo) {
        float[] points = new float[1]; // the lambdas want an immutable variable, hence this array hack
        points[0] = 0.0f;

        for (TokenResult r : tokenResults) {
            r.getUsers().keySet().stream().filter(u -> Objects.equals(u.getId(), photo.getUserId())).forEach(u -> points[0] += r.getUsers().get(u));
            r.getCountries().keySet().stream().filter(c -> Objects.equals(c.getId(), photo.getCountryId())).forEach(c -> points[0] += r.getCountries().get(c));
            r.getLocations().keySet().stream().filter(l -> Objects.equals(l.getId(), photo.getLocationId())).forEach(l -> points[0] += r.getLocations().get(l));
            r.getOperators().keySet().stream().filter(o -> Objects.equals(o.getId(), photo.getOperatorId())).forEach(o -> points[0] += r.getOperators().get(o));
            r.getVehicleClasses().keySet().stream().filter(v -> Objects.equals(v.getId(), photo.getVehicleClassId())).forEach(v -> points[0] += r.getVehicleClasses().get(v));
        }

        return points[0];
    }

    public List<? extends Photo> search(Context context, String freeText) {
        List<String> tokens = tokenize(freeText);
        System.out.println("tokens: " + tokens);

        List<TokenResult> tokenResults = new ArrayList<>();
        for (String token : tokens) {
            String quotedToken = token.contains(" ") ? "\"" + token + "\"" : token;
            tokenResults.add(
                new TokenResult(
                    token,
                    context.getUsersModel().searchFreeText(quotedToken),
                    context.getCountriesModel().searchFreeText(quotedToken),
                    context.getLocationsModel().searchFreeText(quotedToken),
                    context.getOperatorsModel().searchFreeText(quotedToken),
                    context.getVehicleClassesModel().searchFreeText(quotedToken)
                )
            );
        }

        System.out.println(tokenResults);

        List<? extends Photo> photos = new ArrayList<>(context.getPhotosModel().broadSearch(tokenResults));

        Collections.sort(photos, new PhotoRankComparator(tokenResults));

        return photos;
    }

    private class PhotoRankComparator implements Comparator<Photo> {
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
}
