package controllers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.*;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;

import java.util.List;

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

    public Result country(Http.Request request, int page, Integer countryId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, countryId, null, null);
        injector.injectMembers(search);

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        List<? extends Operator> operators = null;
        if (search.getCountryId() != null && search.getOperatorId() == null) {
            operators = context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByCountryId(search.getCountryId())).sorted(LocalizedComparator.get(lang)).toList();
        }

        return ok(views.html.browse.country.render(request, country, operators, search, lastPage, photos, user, lang));
    }

    public Result operator(Http.Request request, int page, Integer countryId, Integer operatorId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        Operator operator = context.getOperatorsModel().get(operatorId);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, countryId, operatorId, null);
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

    public Result vehicleClass(Http.Request request, int page, Integer countryId, Integer operatorId, Integer vehicleClassId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        Operator operator = context.getOperatorsModel().get(operatorId);
        VehicleClass vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, countryId, operatorId, vehicleClassId);
        injector.injectMembers(search);

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        return ok(views.html.browse.vehicleClass.render(request, country, operator, vehicleClass, search, lastPage, photos, user, lang));
    }
}
