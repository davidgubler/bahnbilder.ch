package utils;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Injector;
import models.*;
import models.context.*;
import play.libs.F;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Injector injector;

    private Http.RequestHeader requestHeader;

    private final ClassToInstanceMap<ContextModel> models = MutableClassToInstanceMap.create(new HashMap<>());

    public Context(Injector injector, Http.RequestHeader requestHeader) {
        this.injector = injector;
        this.requestHeader = requestHeader;
    }

    public static Context get(Http.RequestHeader requestHeader) {
        return requestHeader.attrs().get(ContextFilter.CONTEXT);
    }

    public Injector getInjector() {
        return injector;
    }

    public Http.RequestHeader getRequest() {
        return requestHeader;
    }

    public Context getNewBackgroundContext() {
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder();
        requestBuilder.remoteAddress("127.0.0.1");
        return new Context(injector, requestBuilder.build());
    }

    public <T extends ContextModel> T getContextModel(Class<T> clazz) {
        if (!models.containsKey(clazz)) {
            try {
                T model = clazz.getConstructor(Context.class).newInstance(this);
                injector.injectMembers(model);
                models.putInstance(clazz, model);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return models.getInstance(clazz);
    }

    public CalendarOrdersModel getCalendarOrdersModel() {
        return getContextModel(ContextCalendarOrdersModel.class);
    }

    public CountriesModel getCountriesModel() {
        return getContextModel(ContextCountriesModel.class);
    }

    public FilesOriginalModel getFilesOriginalModel() {
        return getContextModel(ContextFilesOriginalModel.class);
    }

    public FilesScaledModel getFilesScaledModel() {
        return getContextModel(ContextFilesScaledModel.class);
    }

    public GeocodingModel getGeocodingModel() {
        return getContextModel(ContextGeocodingModel.class);
    }

    public KeywordsModel getKeywordsModel() {
        return getContextModel(ContextKeywordsModel.class);
    }

    public LicensesModel getLicensesModel() {
        return getContextModel(ContextLicensesModel.class);
    }

    public LocationsModel getLocationsModel() {
        return getContextModel(ContextLocationsModel.class);
    }

    public OperatorsModel getOperatorsModel() {
        return getContextModel(ContextOperatorsModel.class);
    }

    public PhotosModel getPhotosModel() {
        return getContextModel(ContextPhotosModel.class);
    }

    public PhotoTypesModel getPhotoTypesModel() {
        return getContextModel(ContextPhotoTypesModel.class);
    }

    public TraveloguesModel getTraveloguesModel() {
        return getContextModel(ContextTraveloguesModel.class);
    }

    public UsersModel getUsersModel() {
        return getContextModel(ContextUsersModel.class);
    }

    public VehicleClassesModel getVehicleClassesModel() {
        return getContextModel(ContextVehicleClassesModel.class);
    }

    public VehiclePropulsionsModel getVehiclePropulsionsModel() {
        return getContextModel(ContextVehiclePropulsionsModel.class);
    }

    public VehicleSeriesModel getVehicleSeriesModel() {
        return getContextModel(ContextVehicleSeriesModel.class);
    }

    public VehicleTypesModel getVehicleTypesModel() {
        return getContextModel(ContextVehicleTypesModel.class);
    }

    public ViewsModel getViewsModel() {
        return getContextModel(ContextViewsModel.class);
    }

    public VisionModel getVisionModel() {
        return getContextModel(ContextVisionModel.class);
    }

    public WikidataModel getWikidataModel() {
        return getContextModel(ContextWikidataModel.class);
    }

    public Map<String, F.Tuple<Integer, Integer>> getCallSummary() {
        Map<String, F.Tuple<Integer, Integer>> callSummary = new HashMap<>();
        for (ContextModel cm : models.values()) {
            callSummary.put(cm.getClass().getSimpleName(), cm.getCalls());
        }
        return callSummary;
    }
}
