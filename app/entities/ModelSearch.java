package entities;

import com.google.inject.Inject;
import models.*;
import play.mvc.Http;

import java.lang.reflect.Field;

public class ModelSearch extends Search {

    @Inject
    private UsersModel usersModel;

    @Inject
    private LicensesModel licensesModel;

    @Inject
    private PhotoTypesModel photoTypesModel;

    @Inject
    private CountriesModel countriesModel;

    @Inject
    private LocationsModel locationsModel;

    @Inject
    private OperatorsModel operatorsModel;

    @Inject
    private VehicleClassesModel vehicleClassesModel;

    public ModelSearch(Http.Request request) {
        super(request);
    }

    public ModelSearch(int page, Integer country, Integer operator, Integer vclass) {
        super(page, country, operator, vclass);
    }

    public ModelSearch(Integer photoType) {
        super(photoType);
    }

    private User author;

    public User getAuthor() {
        if (author == null) {
            author = usersModel.get(getAuthorId());
        }
        return author;
    }

    private License license;

    public License getLicense() {
        if (license == null) {
            license = licensesModel.get(getLicenseId());
        }
        return license;
    }

    private PhotoType photoType;

    public PhotoType getPhotoType() {
        if (photoType == null) {
            photoType = photoTypesModel.get(getPhotoTypeId());
        }
        return photoType;
    }

    private Country country;

    public Country getCountry() {
        if (country == null) {
            country = countriesModel.get(getCountryId());
        }
        return country;
    }

    private Location location;

    public Location getLocation() {
        if (location == null) {
            location = locationsModel.get(getLocationId());
        }
        return location;
    }

    private Operator operator;

    public Operator getOperator() {
        if (operator == null) {
            operator = operatorsModel.get(getOperatorId());
        }
        return operator;
    }

    private VehicleClass vehicleClass;

    public VehicleClass getVehicleClass() {
        if (vehicleClass == null) {
            vehicleClass = vehicleClassesModel.get(getVehicleClassId());
        }
        return vehicleClass;
    }

    public boolean isActive() {
        return !toQuery().isEmpty() && !inactive;
    }

    @Override
    public String toQuery() {
        String q = "";
        // we need to use the fields of the superclass
        for (Field f : this.getClass().getSuperclass().getDeclaredFields()) {
            String value = querySerialize(f);
            if (value != null) {
                if (!q.isEmpty()) {
                    q += "&";
                }
                q += f.getName() + "=" + value;
            }
        }
        return q;
    }

    @Override
    public String toQueryInactive() {
        boolean inactive = this.inactive;
        this.inactive = true;
        String query = toQuery();
        this.inactive = inactive;
        return query;
    }
}
