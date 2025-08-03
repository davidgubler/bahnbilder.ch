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

    public Result browse(Http.Request request, int page, Integer country, Integer operator, Integer vehicleClass) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        ModelSearch search = new ModelSearch(page, country, operator, vehicleClass);
        injector.injectMembers(search);

        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        List<? extends Country> countries = null;
        if (search.getCountryId() == null) {
            countries = context.getCountriesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        }

        List<? extends Operator> operators = null;
        if (search.getCountryId() != null && search.getOperatorId() == null) {
            operators = context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByCountryId(search.getCountryId())).sorted(LocalizedComparator.get(lang)).toList();
        }

        List<? extends VehicleClass> vehicleClasses = null;
        if (search.getCountryId() != null && search.getOperatorId() != null && search.getVehicleClassId() == null) {
            vehicleClasses = context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByCountryIdOperatorId(search.getCountryId(), search.getOperatorId())).sorted(LocalizedComparator.get(lang)).toList();
        }

        return ok(views.html.browse.list.render(request, countries, operators, vehicleClasses, search, lastPage, photos, user, lang));
    }
}
