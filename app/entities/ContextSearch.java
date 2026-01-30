package entities;

import play.mvc.Http;
import utils.Context;

import java.lang.reflect.Field;

public class ContextSearch extends Search {

    private Context context;

    public ContextSearch(Http.Request request) {
        super(request);
        context = Context.get(request);
    }

    public ContextSearch(Context context, int page, Integer country, Integer operator, Integer vclass) {
        super(page, country, operator, vclass);
        this.context = context;
    }

    public ContextSearch(Context context, Integer photoType) {
        super(photoType);
        this.context = context;
    }

    private User author;

    public User getAuthor() {
        if (author == null) {
            author = context.getUsersModel().get(getAuthorId());
        }
        return author;
    }

    private License license;

    public License getLicense() {
        if (license == null) {
            license = context.getLicensesModel().get(getLicenseId());
        }
        return license;
    }

    private PhotoType photoType;

    public PhotoType getPhotoType() {
        if (photoType == null) {
            photoType = context.getPhotoTypesModel().get(getPhotoTypeId());
        }
        return photoType;
    }

    private Country country;

    public Country getCountry() {
        if (country == null) {
            country = context.getCountriesModel().get(getCountryId());
        }
        return country;
    }

    private Location location;

    public Location getLocation() {
        if (location == null) {
            location = context.getLocationsModel().get(getLocationId());
        }
        return location;
    }

    private Operator operator;

    public Operator getOperator() {
        if (operator == null) {
            operator = context.getOperatorsModel().get(getOperatorId());
        }
        return operator;
    }

    private VehicleClass vehicleClass;

    public VehicleClass getVehicleClass() {
        if (vehicleClass == null) {
            vehicleClass = context.getVehicleClassesModel().get(getVehicleClassId());
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
