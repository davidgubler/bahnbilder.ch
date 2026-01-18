package controllers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.*;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrowseController extends Controller {
    @Inject
    private Injector injector;

    public Result world(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(1, null, null, null);
        injector.injectMembers(search);

        List<? extends Country> countries = null;
        if (search.getCountryId() == null) {
            countries = context.getCountriesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        }

        return ok(views.html.browse.world.render(request, countries, search, user, lang));
    }

    public Result country(Http.Request request, int page, String countryId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Country country = context.getCountriesModel().get(countryId);
        ModelSearch search = new ModelSearch(page, country == null ? null : country.getId(), null, null);
        injector.injectMembers(search);
        if (search.getCountry() == null) {
            throw new NotFoundException("Country");
        }

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        List<? extends Operator> operators = null;
        if (search.getCountryId() != null && search.getOperatorId() == null) {
            operators = context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByCountryId(search.getCountryId())).sorted(LocalizedComparator.get(lang)).toList();
        }

        Integer mostCommonVehicleClass = context.getPhotosModel().getMostCommonVehicleClassByCountry(search.getCountry());
        List<? extends Photo> mostPopularVehicleClassPhotos = context.getPhotosModel().search(new Search(search.getCountry(), mostCommonVehicleClass));

        int vehicleClassCount = context.getPhotosModel().getVehicleClassCountByCountry(search.getCountry());
        int vehicleCount = context.getPhotosModel().getVehicleCountByCountry(search.getCountry());

        List<? extends OperatorVehicleClass> latestVehicleClasses = context.getPhotosModel().getLatestVehicleClassIdAdditionsByCountry(search.getCountry());

        Map<Integer, Integer> operatorCount = context.getPhotosModel().getOperatorCountByCountry(search.getCountry());

        List<Integer> counts = operatorCount.values().stream().sorted((x, y) -> Integer.compare(y, x)).toList();
        int operatorCutOff = Math.min(counts.size() > 15 ? counts.get(11) : 0, 100);

        List<? extends Operator> featuredOperators = operators.stream().filter(o -> operatorCount.get(o.getId()) >= operatorCutOff).toList();
        operators = new ArrayList<>(operators);
        operators.removeAll(featuredOperators);
        Map<Operator, List<? extends OperatorEra>> eras = featuredOperators.stream().collect(Collectors.toMap(o -> o, o -> o.getEras()));

        return ok(views.html.browse.country.render(request, mostPopularVehicleClassPhotos.get(0), featuredOperators, eras, operators, vehicleClassCount, vehicleCount, count, latestVehicleClasses, search, lastPage, photos, user, lang));
    }

    public Result operator(Http.Request request, int page, String countryId, Integer operatorId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        Operator operator = context.getOperatorsModel().get(operatorId);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, country == null ? null : country.getId(), operatorId, null);
        injector.injectMembers(search);

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        List<? extends VehicleClass> vehicleClasses = null;
        if (search.getCountryId() != null && search.getOperatorId() != null && search.getVehicleClassId() == null) {
            vehicleClasses = context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByCountryIdOperatorId(search.getCountryId(), search.getOperatorId())).sorted(LocalizedComparator.get(lang)).toList();
        }

        return ok(views.html.browse.operator.render(request, country, operator, vehicleClasses, search, lastPage, photos, user, lang));
    }

    public Result vehicleClass(Http.Request request, int page, String countryId, Integer operatorId, Integer vehicleClassId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        Operator operator = context.getOperatorsModel().get(operatorId);
        VehicleClass vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, country == null ? null : country.getId(), operatorId, vehicleClassId);
        injector.injectMembers(search);

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        return ok(views.html.browse.vehicleClass.render(request, country, operator, vehicleClass, search, lastPage, photos, user, lang));
    }
}
