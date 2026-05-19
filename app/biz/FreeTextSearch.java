package biz;

import entities.*;
import org.apache.pekko.http.javadsl.model.headers.AcceptRanges;
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

    private Map<? extends User, Float> searchUsers(Context context, String freeText) {
        Map<? extends User, Float> users = context.getUsersModel().searchFreeText(freeText);
        //if (users.isEmpty() && freeText.contains("\"")) {
        //    users = context.getUsersModel().searchFreeText(removeQuoted(freeText));
        //}
        return users;
    }

    private Map<? extends Country, Float> searchCountries(Context context, String freeText) {
        Map<? extends Country, Float> countries = context.getCountriesModel().searchFreeText(freeText);
        //if (countries.isEmpty() && freeText.contains("\"")) {
        //    countries = context.getCountriesModel().searchFreeText(removeQuoted(freeText));
        //}
        // FIXME: Also search by country code
        return countries;
    }

    private Map<? extends Location, Float> searchLocations(Context context, String freeText) {
        Map<? extends Location, Float> locations = context.getLocationsModel().searchFreeText(freeText);
        //if (locations.isEmpty() && freeText.contains("\"")) {
        //    locations = context.getLocationsModel().searchFreeText(removeQuoted(freeText));
        //}
        return locations;
    }

    private Map<? extends Operator, Float> searchOperators(Context context, String freeText) {
        Map<? extends Operator, Float> operators = context.getOperatorsModel().searchFreeText(freeText);
        //if (operators.isEmpty() && freeText.contains("\"")) {
        //    operators = context.getOperatorsModel().searchFreeText(removeQuoted(freeText));
        //}
        // FIXME: Also search by abbreviation
        return operators;
    }

    private Map<? extends VehicleClass, Float> searchVehicleClasses(Context context, String freeText) {
        Map<? extends VehicleClass, Float> vehicleClasses = context.getVehicleClassesModel().searchFreeText(freeText);
        //if (vehicleClasses.isEmpty() && freeText.contains("\"")) {
        //    vehicleClasses = context.getVehicleClassesModel().searchFreeText(removeQuoted(freeText));
        //}
        return vehicleClasses;
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

    public List<? extends Photo> search(Context context, String freeText) {
        List<String> tokens = tokenize(freeText);
        System.out.println("tokens: " + tokens);

        List<TokenResult> tokenResults = new ArrayList<>();
        for (String token : tokens) {
            String quotedToken = token.contains(" ") ? "\"" + token + "\"" : token;
            tokenResults.add(new TokenResult(
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

        List<? extends Photo> photos = context.getPhotosModel().broadSearch(tokenResults);

        return photos;
    }
}
