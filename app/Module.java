import biz.*;
import com.google.inject.AbstractModule;
import models.*;
import models.google.GoogleGeocodingModel;
import models.hardcoded.HardcodedLicensesModel;
import models.mongodb.*;
import services.*;
import utils.Config;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(MongoDb.class).asEagerSingleton();
        bind(Mail.class).asEagerSingleton();
        bind(Railinfo.class).asEagerSingleton();
        bind(Travelogues.class);
        bind(Photos.class);
        bind(Calendars.class);
        bind(Countries.class);
        bind(Operators.class);
        bind(VehicleClasses.class);
        bind(VehicleSeries.class);
        bind(Keywords.class);
        bind(UsersModel.class).to(MongoDbUsersModel.class).asEagerSingleton();
        bind(CountriesModel.class).to(MongoDbCountriesModel.class).asEagerSingleton();
        bind(CalendarOrdersModel.class).to(MongoDbCalendarOrdersModel.class).asEagerSingleton();
        bind(KeywordsModel.class).to(MongoDbKeywordsModel.class).asEagerSingleton();
        bind(OperatorsModel.class).to(MongoDbOperatorsModel.class).asEagerSingleton();
        bind(PhotoTypesModel.class).to(MongoDbPhotoTypesModel.class).asEagerSingleton();
        bind(LocationsModel.class).to(MongoDbLocationsModel.class).asEagerSingleton();
        bind(TraveloguesModel.class).to(MongoDbTraveloguesModel.class).asEagerSingleton();
        bind(VehicleClassesModel.class).to(MongoDbVehicleClassesModel.class).asEagerSingleton();
        bind(VehiclePropulsionsModel.class).to(MongoDbVehiclePropulsionsModel.class).asEagerSingleton();
        bind(VehicleTypesModel.class).to(MongoDbVehicleTypesModel.class).asEagerSingleton();
        bind(VehicleSeriesModel.class).to(MongoDbVehicleSeriesModel.class).asEagerSingleton();
        bind(WikidataModel.class).to(MongoDbWikidataModel.class).asEagerSingleton();
        bind(PhotosModel.class).to(MongoDbPhotosModel.class).asEagerSingleton();
        bind(ViewsModel.class).to(MongoDbViewsModel.class).asEagerSingleton();
        bind(RequestsDailyModel.class).to(MongoDbRequestsDailyModel.class).asEagerSingleton();
        bind(LicensesModel.class).to(HardcodedLicensesModel.class);
        bind(GeocodingModel.class).to(GoogleGeocodingModel.class);
        bind(Jobs.class).asEagerSingleton();
        if (Config.Option.LIVEFILES_HOSTNAME.get() != null) {
            bind(LiveFiles.class).asEagerSingleton();
        }

        bind(FilesScaledModel.class).to(MongoDbFilesScaledModel.class);
        bind(FilesOriginalModel.class).to(MongoDbFilesOriginalModel.class);
    }
}
