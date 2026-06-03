package entities.search;

import entities.Country;
import entities.User;
import play.mvc.Http;
import utils.Config;
import utils.InputUtils;
import utils.geometry.Point;
import utils.geometry.SimplePoint;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Search {

    public enum SortBy { views, uploadDate, photoDate, rating, photoType;
        public static SortBy fromString(String input) {
            if ("views".equalsIgnoreCase(input)) {
                return views;
            }
            if ("uploadDate".equalsIgnoreCase(input)) {
                return uploadDate;
            }
            if ("rating".equalsIgnoreCase(input)) {
                return rating;
            }
            if ("photoType".equalsIgnoreCase(input)) {
                return photoType;
            }
            return photoDate;
        }

        public static SortBy fromString(String[] input) {
            if (input == null || input.length == 0) {
                return photoDate;
            }
            return fromString(input[0]);
        }
    }

    /* Variable names are as they appear in the URL, not according to Java style.
     * Be aware that changing them breaks existing links!
     */
    private boolean inactive;
    private String photographer;
    private String description;
    private LocalDate datefrom;
    private LocalDate dateto;
    private Integer author;
    private Integer phototype;
    private Integer country;
    private Integer location;
    private Integer operator;
    private Integer vclass;
    private Integer nr;
    private Integer license;
    private Double lat;
    private Double lng;
    private double radiusKm = Config.PHOTO_SPOT_RADIUS_KM;
    private List<String> labels;
    private List<String> keywords;
    private boolean series;
    private boolean portraitsFirst;
    private boolean withLocationOnly;
    private SortBy sortBy = SortBy.photoDate;
    private String q;
    private int page = 1;
    private int resultsPerPage = 20;

    public Search(Http.Request request) {
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            inactive = InputUtils.toBoolean(data.get("inactive"));
            String photographerTmp = InputUtils.trimToNull(data.get("photographer"));
            if (photographerTmp == null && InputUtils.toInt(data.get("author")) == null) {
                photographerTmp = InputUtils.trimToNull(data.get("author"));
            }
            photographer = photographerTmp;
            description = InputUtils.trimToNull(data.get("description"));
            datefrom = InputUtils.parseDate(data.get("datefrom"));
            dateto = InputUtils.parseDate(data.get("dateto"));
            author = InputUtils.toInt(data.get("author"));
            phototype = InputUtils.toInt(data.get("phototype"));
            location = InputUtils.toInt(data.get("location"));
            country = InputUtils.toInt(data.get("country"));
            operator = InputUtils.toInt(data.get("operator"));
            vclass = InputUtils.toInt(data.get("vclass"));
            nr = InputUtils.toInt(data.get("nr"));
            license = InputUtils.toInt(data.get("license"));
            lat = InputUtils.toDouble(data.get("lat"));
            lng = InputUtils.toDouble(data.get("lng"));
            labels = InputUtils.toListOfStrings(data.get("labels"), ",");
            keywords = InputUtils.toListOfStrings(data.get("keywords"), ",");
            series = InputUtils.toBoolean(data.get("series"));
            portraitsFirst = InputUtils.toBoolean(data.get("portraitsFirst"));
            withLocationOnly = InputUtils.toBoolean(data.get("withLocationOnly"));
            sortBy = SortBy.fromString(data.get("sortBy"));
            q = InputUtils.trimToNull(data.get("q"));
        } else {
            inactive = InputUtils.toBoolean(request.queryString("inactive").orElse(null));
            String photographerTmp = InputUtils.trimToNull(request.queryString("photographer").orElse(null));
            if (photographerTmp == null && InputUtils.toInt(request.queryString("author").orElse(null)) == null) {
                photographerTmp = InputUtils.trimToNull(request.queryString("author").orElse(null));
            }
            photographer = photographerTmp;
            description = InputUtils.trimToNull(request.queryString("description").orElse(null));
            datefrom = InputUtils.parseDate(request.queryString("datefrom").orElse(null));
            dateto = InputUtils.parseDate(request.queryString("dateto").orElse(null));
            author = InputUtils.toInt(request.queryString("author").orElse(null));
            Integer pictureType = InputUtils.toInt(request.queryString("pictureType").orElse(null)); // compatibility
            phototype = pictureType == null ? InputUtils.toInt(request.queryString("phototype").orElse(null)) : pictureType;
            location = InputUtils.toInt(request.queryString("location").orElse(null));
            country = InputUtils.toInt(request.queryString("country").orElse(null));
            operator = InputUtils.toInt(request.queryString("operator").orElse(null));
            vclass = InputUtils.toInt(request.queryString("vclass").orElse(null));
            nr = InputUtils.toInt(request.queryString("nr").orElse(null));
            license = InputUtils.toInt(request.queryString("license").orElse(null));
            lat = InputUtils.toDouble(request.queryString("lat").orElse(null));
            lng = InputUtils.toDouble(request.queryString("lng").orElse(null));
            labels = InputUtils.toListOfStrings(request.queryString("labels").orElse(null), ",");
            keywords = InputUtils.toListOfStrings(request.queryString("keywords").orElse(null), ",");
            series = InputUtils.toBoolean(request.queryString("series").orElse(null));
            portraitsFirst = InputUtils.toBoolean(request.queryString("portraitsFirst").orElse(null));
            withLocationOnly = InputUtils.toBoolean(request.queryString("withLocationOnly").orElse(null));
            sortBy = SortBy.fromString(request.queryString("sortBy").orElse(null));
            q = InputUtils.trimToNull(request.queryString("q").orElse(null));
            Integer pageFromReq = InputUtils.toInt(request.queryString("page").orElse(null));
            page = pageFromReq == null ? 1 : pageFromReq;
        }
    }

    public Search() {
    }

    public Search(SortBy sortBy, int page) {
        this.sortBy = sortBy;
        this.page = page;
    }

    public Search(int page, Integer country, Integer operator, Integer vclass) {
        this.country = country;
        this.operator = operator;
        this.vclass = vclass;
        this.page = page;
    }

    public Search(Integer photoType) {
        this.phototype = photoType;
    }

    public Search(Integer country, Point point, double radiusKm) {
        this.country = country;
        lat = point == null ? null : point.getLat();
        lng = point == null ? null : point.getLng();
        this.radiusKm = radiusKm;
    }

    // used by vehicle overview page
    public Search(Integer operatorId, Integer vehicleClassId) {
        operator = operatorId;
        vclass = vehicleClassId;
    }

    // used for searching for numbers
    public Search(Integer operatorId, Integer vehicleClassId, Integer nr) {
        operator = operatorId;
        vclass = vehicleClassId;
        this.nr = nr;
    }

    public Search(Search search) {
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                f.set(this, f.get(search));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Search withNr(Integer nr) {
        Search search = new Search(this);
        search.nr = nr;
        return search;
    }

    public Search withResultsPerPage(int resultsPerPage) {
        Search search = new Search(this);
        search.resultsPerPage = resultsPerPage;
        return search;
    }

    public Search withInactive(boolean inactive) {
        Search search = new Search(this);
        search.inactive = inactive;
        return search;
    }

    // used for operator overview
    public Search(Integer operatorId, LocalDate inception, LocalDate dissolved) {
        datefrom = inception;
        dateto = dissolved;
        operator = operatorId;
    }

    // used by IncompleteSearch
    protected Search(User user, int page) {
        author = user.getId();
        this.page = page;
    }

    // used by Browse country
    public Search(Country country, Integer vclass) {
        this.country = country.getId();
        this.vclass = vclass;
    }

    public boolean getInactive() {
        return inactive;
    }

    public String getPhotographer() {
        return photographer;
    }

    public Integer getAuthorId() {
        return author;
    }

    public Integer getLicenseId() {
        return license;
    }

    public LocalDate getDateFrom() {
        return datefrom;
    }

    public LocalDate getDateTo() {
        return dateto;
    }

    public Integer getPhotoTypeId() {
        return phototype;
    }

    public Integer getCountryId() {
        return country;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public double getRadiusKm() {
        return radiusKm;
    }

    public SimplePoint getCoordinates() {
        if (lng == null || lat == null) {
            return null;
        }
        return new SimplePoint.PointBuilder().withLng(lng).withLat(lat).build();
    }

    public Integer getLocationId() {
        return location;
    }

    public Integer getOperatorId() {
        return operator;
    }

    public Integer getVehicleClassId() {
        return vclass;
    }

    public boolean getIncludeVehicleSeries() {
        return series;
    }

    public Integer getNr() {
        return nr;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getKeywordsInclude() {
        return keywords.stream().filter(k -> !k.startsWith("-")).sorted().collect(Collectors.toUnmodifiableList());
    }

    public List<String> getKeywordsExclude() {
        return keywords.stream().filter(k -> k.startsWith("-")).map(k -> k.substring(1)).sorted().collect(Collectors.toUnmodifiableList());
    }

    public String getFreeText() {
        return q;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public int getLastPage(long resultsCount) {
        int lastPage = (int)(resultsCount / resultsPerPage + (resultsCount % resultsPerPage > 0 ? 1 : 0));
        if (lastPage < 1) {
            lastPage = 1;
        }
        return lastPage;
    }

    public void adjustPage(int lastPage) {
        if (page > lastPage) {
            page = lastPage;
        }
    }

    public int getPage() {
        return page;
    }

    protected String querySerialize(Field f) {
        try {
            if (f.getType() == String.class) {
                String x = (String)f.get(this);
                return x == null ? null : URLEncoder.encode(x, Charset.forName("UTF-8"));
            }
            if (f.getType() == Integer.class || f.getType() == LocalDate.class || f.getType() == Double.class) {
                Object x = f.get(this);
                return x == null ? null : x.toString();
            }
            if (f.getType() == boolean.class) {
                return f.getBoolean(this) ? Boolean.TRUE.toString() : null;
            }
            if (f.getType() == List.class) {
                List<Object> l = (List)f.get(this);
                if (l == null || l.isEmpty()) {
                    return null;
                }
                return URLEncoder.encode(l.stream().map(o -> o.toString()).collect(Collectors.joining(",")), Charset.forName("UTF-8"));
            }
            if (f.getType() == SortBy.class) {
                SortBy sortBy = (SortBy)f.get(this);
                return sortBy == SortBy.photoDate ? null : sortBy.name();
            }
        } catch (IllegalAccessException e) {
            return null;
        }
        return null;
    }

    public String toQuery() {
        String q = "";
        for (Field f : this.getClass().getDeclaredFields()) {
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

    public String toQueryInactive() {
        boolean inactive = this.inactive;
        this.inactive = true;
        String query = toQuery();
        this.inactive = inactive;
        return query;
    }
}

