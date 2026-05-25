package entities.search;

import entities.*;

import java.util.Map;

public class TokenResult {
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
