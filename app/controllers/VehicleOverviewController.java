package controllers;

import entities.*;
import entities.tmp.OtherVehicleType;
import entities.tmp.VehicleClassSummary;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;


public class VehicleOverviewController extends Controller {

    public Result vehicleClass(Http.Request request, Integer vehicleClassId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        VehicleClass vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        if (vehicleClass == null) {
            throw new NotFoundException("Class");
        }
        VehicleSeries vehicleSeries = context.getVehicleSeriesModel().get(vehicleClass.getVehicleSeriesId());
        List<? extends Operator> operators = context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByVehicleClassIds(List.of(vehicleClass.getId()))).sorted(LocalizedComparator.get(lang)).toList();
        Map<Operator, List<? extends Country>> countriesByOperator = new HashMap<>();

        Map<Operator, VehicleClassSummary> vehicleClassSummaryByOperator = new HashMap<>();

        for (Operator operator : operators) {
            List<? extends Country> countries = context.getCountriesModel().getByIds(context.getPhotosModel().getCountryIdsByOperatorAndVehicleClassIds(operator.getId(), List.of(vehicleClass.getId()))).sorted(LocalizedComparator.get(lang)).toList();
            countriesByOperator.put(operator, countries);

            Search search = new Search(operator.getId(), vehicleClass.getId());
            List<Integer> nrs = context.getPhotosModel().getNrsByOperatorAndVehicleClassId(operator.getId(), vehicleClass.getId());
            vehicleClassSummaryByOperator.put(operator, new VehicleClassSummary(context.getPhotosModel().search(search).get(0), search, context.getPhotosModel().searchCount(search), vehicleClass, vehicleSeries, nrs));
        }
        return ok(views.html.vehicleOverview.vehicleClass.render(request, vehicleClass, vehicleSeries, operators, countriesByOperator, vehicleClassSummaryByOperator, user, lang));
    }

    public Result vehicleSeriesList(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        List<? extends VehicleSeries> vehicleSeries = context.getVehicleSeriesModel().getAll().sorted(LocalizedComparator.get(lang)).collect(Collectors.toUnmodifiableList());
        return ok(views.html.vehicleOverview.vehicleSeriesList.render(request, vehicleSeries, user, lang));
    }

    public Result vehicleSeries(Http.Request request, Integer vehicleSeriesId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        VehicleSeries vehicleSeries = context.getVehicleSeriesModel().get(vehicleSeriesId);
        if (vehicleSeries == null) {
            throw new NotFoundException("Class");
        }

        List<VehicleClass> vehicleClasses = context.getVehicleClassesModel().getByVehicleSeriesId(vehicleSeries.getId()).sorted(LocalizedComparator.get(lang)).collect(Collectors.toUnmodifiableList());
        List<Integer> vehicleClassIds = vehicleClasses.stream().map(VehicleClass::getId).collect(Collectors.toUnmodifiableList());
        List<? extends Operator> operators = context.getOperatorsModel().getByIds(context.getPhotosModel().getOperatorIdsByVehicleClassIds(vehicleClassIds)).sorted(LocalizedComparator.get(lang)).toList();

        Map<Operator, List<? extends Country>> countriesByOperator = new HashMap<>();
        Map<Operator, List<VehicleClassSummary>> vehicleClassSummariesByOperator = new HashMap<>();

        for (Operator operator : operators) {
            List<? extends Country> countries = context.getCountriesModel().getByIds(context.getPhotosModel().getCountryIdsByOperatorAndVehicleClassIds(operator.getId(), vehicleClassIds)).sorted(LocalizedComparator.get(lang)).toList();
            countriesByOperator.put(operator, countries);

            List<VehicleClassSummary> summaries = new ArrayList<>();
            for (VehicleClass vehicleClass : vehicleClasses) {
                Search search = new Search(operator.getId(), vehicleClass.getId());
                long count = context.getPhotosModel().searchCount(search);
                if (count > 0) {
                    List<Integer> nrs = context.getPhotosModel().getNrsByOperatorAndVehicleClassId(operator.getId(), vehicleClass.getId());
                    summaries.add(new VehicleClassSummary(context.getPhotosModel().search(search).get(0), search, count, vehicleClass, vehicleSeries, nrs));
                }
            }
            vehicleClassSummariesByOperator.put(operator, summaries);
        }
        return ok(views.html.vehicleOverview.vehicleSeries.render(request, vehicleSeries, operators, countriesByOperator, vehicleClassSummariesByOperator, user, lang));
    }

    public Result operator(Http.Request request, Integer operatorId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Operator operator = context.getOperatorsModel().get(operatorId);
        if (operator == null) {
            throw new NotFoundException("Operator");
        }
        List<? extends Country> countries = context.getCountriesModel().getByIds(context.getPhotosModel().getCountryIdsByOperatorId(operator.getId())).sorted(LocalizedComparator.get(lang)).collect(Collectors.toUnmodifiableList());

        List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getByIds(context.getPhotosModel().getVehicleClassIdsByOperatorId(operator.getId())).sorted(LocalizedComparator.get(lang)).collect(Collectors.toUnmodifiableList());

        final VehicleType otherVehicleType = new OtherVehicleType();

        Map<VehicleType, List<VehicleClassSummary>> summariesByVehicleType = new HashMap<>();
        for (VehicleClass vehicleClass : vehicleClasses) {
            Search search = new Search(operator.getId(), vehicleClass.getId());
            long count = context.getPhotosModel().searchCount(search);
            if (count > 0) {
                List<Integer> nrs = context.getPhotosModel().getNrsByOperatorAndVehicleClassId(operator.getId(), vehicleClass.getId());
                VehicleType vehicleType = vehicleClass.getVehicleType();
                if (vehicleType == null) {
                    vehicleType = otherVehicleType;
                }
                if (!summariesByVehicleType.containsKey(vehicleType)) {
                    summariesByVehicleType.put(vehicleType, new ArrayList<>());
                }
                summariesByVehicleType.get(vehicleType).add(new VehicleClassSummary(context.getPhotosModel().search(search).get(0), search, count, vehicleClass, vehicleClass.getVehicleSeries(), nrs));
            }
        }

        List<? extends VehicleType> vehicleTypes = summariesByVehicleType.keySet().stream().sorted().collect(Collectors.toUnmodifiableList());
        return ok(views.html.vehicleOverview.operator.render(request, operator, countries, operator.getEras(), vehicleTypes, summariesByVehicleType, user, lang));
    }
}
