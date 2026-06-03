package entities.search;

import biz.FreeTextSearch;
import entities.*;
import play.mvc.Http;
import utils.Context;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

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
        return !toQuery().isEmpty() && !getInactive();
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
        return new Search(this).withInactive(true).toQuery();
    }

    private List<TokenResult> freeTextSearchTokenResults = null;

    private List<String> tokenize(String freeText) {
        List<String> tokens = new ArrayList<>();
        boolean quoted = false;
        for( String s : freeText.split("\"") ) {
            if (quoted) {
                tokens.add(s);
            } else {
                tokens.addAll(Arrays.asList(s.split(" ")));
            }
            quoted = !quoted;
        }
        return tokens.stream().map(t -> t.trim()).filter(t -> !t.isEmpty()).toList();
    }

    public List<TokenResult> getFreeTextSearchTokenResults() {
        if (freeTextSearchTokenResults == null) {
            List<String> tokens = tokenize(getFreeText());

            List<TokenResult> tokenResults = new ArrayList<>();
            for (String token : tokens) {
                String quotedToken = token.contains(" ") ? "\"" + token + "\"" : token;

                Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<NumIdEntity, Float>> results = new HashMap<>();
                for (FreeTextSearch.SearchCriterion sc : FreeTextSearch.SEARCH_CRITERIA) {
                    results.put(sc, sc.search(context, quotedToken));
                }

                tokenResults.add(new TokenResult(token, results));
            }
            this.freeTextSearchTokenResults = tokenResults;
        }
        return freeTextSearchTokenResults;
    }

    public String getFreeTextActive() {
        return StringUtils.join(getFreeTextSearchTokenResults().stream().filter(tr -> !tr.ignored()).map(tr -> tr.getToken()).toList(), " ");
    }

    public List<String> getFreeTextInactive() {
        return getFreeTextSearchTokenResults().stream().filter(tr -> tr.ignored()).map(tr -> tr.getToken()).toList();
    }
}
