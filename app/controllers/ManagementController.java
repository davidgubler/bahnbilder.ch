package controllers;

import biz.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.*;
import entities.formdata.*;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;
import utils.Context;
import utils.NotAllowedException;
import utils.TriFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ManagementController extends Controller {
    @Inject
    private Countries countries;

    @Inject
    private Operators operators;

    @Inject
    private VehicleClasses vehicleClasses;

    @Inject
    private biz.VehicleSeries vehicleSeries;

    @Inject
    private Keywords keywords;

    @Inject
    private Injector injector;

    public Result manage(Http.Request request, Integer countryId, Integer operatorId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends Country> countries = context.getCountriesModel().getByIds(context.getPhotosModel().getUsedCountryIds()).sorted(LocalizedComparator.get(lang)).toList();
        Country country = context.getCountriesModel().get(countryId);
        List<? extends Operator> operators = country == null ? null : context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByCountryId(country.getId())).sorted(LocalizedComparator.get(lang)).toList();
        Operator operator = context.getOperatorsModel().get(operatorId);
        List<? extends VehicleClass> vehicleClasses = (country == null || operator == null) ? null : context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByCountryIdOperatorId(country.getId(), operator.getId())).sorted(LocalizedComparator.get(lang)).toList();

        List<? extends Country> unusedCountries = context.getCountriesModel().getNotInIds(context.getPhotosModel().getUsedCountryIds()).sorted(LocalizedComparator.get(lang)).toList();
        List<? extends Operator> unusedOperators = context.getOperatorsModel().getNotInIds(context.getPhotosModel().getUsedOperatorIds()).sorted(LocalizedComparator.get(lang)).toList();
        List<? extends VehicleClass> unusedVehicleClasses = context.getVehicleClassesModel().getNotInIds(context.getPhotosModel().getUsedVehicleClassIds()).sorted(LocalizedComparator.get(lang)).toList();

        return ok(views.html.manage.coc.render(request, countries, country, operators, operator, vehicleClasses, unusedCountries, unusedOperators, unusedVehicleClasses, user, lang));
    }


    private <T extends FormData> Result createOrUpdate(Http.Request request, T formData, TriFunction<User, T, Map<String, String>, Content> form) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        return ok(form.apply(user, formData, Collections.emptyMap()));
    }

    private <T extends FormData> Result createOrUpdatePost(Http.Request request, T formData, CUDBusinessLogic businessLogic, TriFunction<User, T, Map<String, String>, Content> form) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        try {
            if (formData.entity == null) {
                businessLogic.create(context, formData, user);
            } else {
                businessLogic.update(context, formData, user);
            }
            return redirect(formData.returnUrl);
        } catch (ValidationException e) {
            return ok(form.apply(user, formData, e.getErrors()));
        }
    }

    // =======
    // Country
    // =======
    public Result createOrUpdateCountry(Http.Request request, String returnUrl, Integer countryId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        return createOrUpdate(request, new CountryFormData(request, returnUrl, country),
                (user, data, errors) -> views.html.manage.editCountry.render(request, data, errors, user));
    }

    public Result createOrUpdateCountryPost(Http.Request request, String returnUrl, Integer countryId) {
        Context context = Context.get(request);
        Country country = context.getCountriesModel().get(countryId);
        return createOrUpdatePost(request, new CountryFormData(request, returnUrl, country), countries,
                (user, data, errors) -> views.html.manage.editCountry.render(request, data, errors, user));
    }

    // ========
    // Operator
    // ========
    public Result createOrUpdateOperator(Http.Request request, String returnUrl, Integer operatorId) {
        Context context = Context.get(request);
        Operator operator = context.getOperatorsModel().get(operatorId);
        return createOrUpdate(request, new OperatorFormData(request, returnUrl, operator),
                (user, data, errors) -> views.html.manage.editOperator.render(request, data, errors, user));
    }

    public Result createOrUpdateOperatorPost(Http.Request request, String returnUrl, Integer operatorId) {
        Context context = Context.get(request);
        Operator operator = context.getOperatorsModel().get(operatorId);
        return createOrUpdatePost(request, new OperatorFormData(request, returnUrl, operator), operators,
                (user, data, errors) -> views.html.manage.editOperator.render(request, data, errors, user));
    }

    public Result deleteOperator(Http.Request request, String returnUrl, Integer operatorId) {
        Context context = Context.get(request);
        Operator operator = context.getOperatorsModel().get(operatorId);
        return ok();
    }

    public Result deleteOperatorPost(Http.Request request, String returnUrl, Integer operatorId) {
        Context context = Context.get(request);
        Operator operator = context.getOperatorsModel().get(operatorId);
        return ok();
    }

    // ============
    // VehicleClass
    // ============
    public Result createOrUpdateVehicleClass(Http.Request request, String returnUrl, Integer vehicleClassId) {
        Context context = Context.get(request);
        VehicleClass vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        return createOrUpdate(request, new VehicleClassFormData(request, returnUrl, vehicleClass),
                (user, data, errors) -> views.html.manage.editVehicleClass.render(
                        request,
                        data,
                        context.getVehicleSeriesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        context.getVehicleTypesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        context.getVehiclePropulsionsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        errors,
                        user));
    }

    public Result createOrUpdateVehicleClassPost(Http.Request request, String returnUrl, Integer vehicleClassId) {
        Context context = Context.get(request);
        VehicleClass vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        return createOrUpdatePost(request, new VehicleClassFormData(request, returnUrl, vehicleClass), vehicleClasses,
                (user, data, errors) -> views.html.manage.editVehicleClass.render(
                        request,
                        data,
                        context.getVehicleSeriesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        context.getVehicleTypesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        context.getVehiclePropulsionsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList(),
                        errors,
                        user));
    }

    // =============
    // VehicleSeries
    // =============
    public Result createOrUpdateVehicleSeries(Http.Request request, String returnUrl, Integer vehicleSeriesId) {
        Context context = Context.get(request);
        entities.VehicleSeries vehicleSeries = context.getVehicleSeriesModel().get(vehicleSeriesId);
        return createOrUpdate(request, new VehicleSeriesFormData(request, returnUrl, vehicleSeries),
                (user, data, errors) -> views.html.manage.editVehicleSeries.render(
                        request,
                        data,
                        errors,
                        user));
    }

    public Result createOrUpdateVehicleSeriesPost(Http.Request request, String returnUrl, Integer vehicleSeriesId) {
        Context context = Context.get(request);
        entities.VehicleSeries vehicleSeries = context.getVehicleSeriesModel().get(vehicleSeriesId);
        return createOrUpdatePost(request, new VehicleSeriesFormData(request, returnUrl, vehicleSeries), this.vehicleSeries,
                (user, data, errors) -> views.html.manage.editVehicleSeries.render(
                        request,
                        data,
                        errors,
                        user));
    }

    // =============
    // Keywords
    // =============
    public Result createOrUpdateKeyword(Http.Request request, String returnUrl, Integer keywordId) {
        Context context = Context.get(request);
        Keyword keyword = context.getKeywordsModel().get(keywordId);
        return createOrUpdate(request, new KeywordFormData(request, returnUrl, keyword),
                (user, data, errors) -> views.html.manage.editKeyword.render(request, data, errors, user));
    }

    public Result createOrUpdateKeywordPost(Http.Request request, String returnUrl, Integer keywordId) {
        Context context = Context.get(request);
        Keyword keyword = context.getKeywordsModel().get(keywordId);
        return createOrUpdatePost(request, new KeywordFormData(request, returnUrl, keyword), keywords,
                (user, data, errors) -> views.html.manage.editKeyword.render(request, data, errors, user));
    }


    public Result manageSeries(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends entities.VehicleSeries> seriesList = context.getVehicleSeriesModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        return ok(views.html.manage.series.render(request, seriesList, user, lang));
    }

    public Result manageNoSeries(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getNoSeries().sorted(LocalizedComparator.get(lang)).toList();
        return ok(views.html.manage.noSeries.render(request, vehicleClasses, user, lang));
    }

    public Result manageNoTypeProp(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getNoTypeProp().sorted(LocalizedComparator.get(lang)).toList();
        return ok(views.html.manage.noTypeProp.render(request, vehicleClasses, user, lang));
    }

    public Result manageNoWikidata(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends Operator> operators = context.getOperatorsModel().getNoWikidata().sorted(LocalizedComparator.get(lang)).toList();
        return ok(views.html.manage.noWikidata.render(request, operators, user, lang));
    }

    public Result manageKeywords(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends Keyword> keywords = context.getKeywordsModel().getAll().sorted(LocalizedComparator.get(lang)).toList();
        return ok(views.html.manage.keywords.render(request, keywords, user, lang));
    }
}
