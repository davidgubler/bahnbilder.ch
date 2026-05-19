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
