package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.*;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.Json;

import java.util.*;

public class SearchController extends Controller {

    @Inject
    private Injector injector;

    public Result search(Http.Request request, String dateFrom, String dateTo) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        ModelSearch search = new ModelSearch(request);
        injector.injectMembers(search);

        List<? extends User> authors = context.getUsersModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        List<? extends License> licenses = context.getLicensesModel().getAll();
        List<? extends PhotoType> photoTypes = context.getPhotoTypesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        List<? extends Country> countries = context.getCountriesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        List<? extends Location> locations = (search.getCountryId() == null) ? null : context.getLocationsModel().get(context.getPhotosModel().getLocationIdsByCountryId(search.getCountryId())).sorted(LocalizedComparator.get(lang)).toList();
        List<? extends Operator> operators = context.getOperatorsModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        List<? extends Keyword> keywords = context.getKeywordsModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        Map<Keyword, Boolean> keywordSelection = context.getKeywordsModel().getKeywordsMap(search.getKeywords());

        long resultsCount = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(resultsCount);
        search.adjustPage(lastPage);

        List<? extends Photo> photos = context.getPhotosModel().search(search);

        return ok(views.html.search.search.render(request, search, authors, licenses, photoTypes, countries, locations, operators, vehicleClasses, keywords, keywordSelection, lastPage, photos, user, lang));
    }

    public Result searchPost(Http.Request request) {
        Context context = Context.get(request);
        Search search = new Search(request);
        return redirect("/search?" + search.toQuery());
    }

    public Result locations(Http.Request request, Integer countryId) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        List<? extends Location> locations = context.getLocationsModel().get(context.getPhotosModel().getLocationIdsByCountryId(countryId)).sorted(LocalizedComparator.get(lang)).toList();
        try {
            return ok(Json.MAPPER.writeValueAsBytes(locations)).as(Http.MimeTypes.JSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
