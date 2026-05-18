package biz;

import entities.*;
import utils.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FreeTextSearch {
    public List<? extends Photo> search(Context context, String freeText) {
        List tokens = Arrays.stream(freeText.split(" ")).map(t -> t.trim()).toList();

        Map<? extends User, Float> users = context.getUsersModel().searchFreeText(freeText);
        Map<? extends Country, Float> countries = context.getCountriesModel().searchFreeText(freeText);
        Map<? extends Location, Float> locations = context.getLocationsModel().searchFreeText(freeText);
        Map<? extends Operator, Float> operators = context.getOperatorsModel().searchFreeText(freeText);
        Map<? extends VehicleClass, Float> vehicleClasses = context.getVehicleClassesModel().searchFreeText(freeText);
        System.out.println("search matches users? " + users);
        System.out.println("search matches countries? " + countries);
        System.out.println("search matches locations? " + locations);
        System.out.println("search matches operators? " + operators);
        System.out.println("search matches vehicleClasses? " + vehicleClasses);

        List<? extends Photo> photos = context.getPhotosModel().broadSearch(users.keySet(), countries.keySet(), locations.keySet(), operators.keySet(), vehicleClasses.keySet());

        return photos;
    }
}
