package biz;

import entities.*;
import entities.VehicleSeries;
import entities.search.ContextSearch;
import entities.search.TokenResult;
import play.libs.F;
import utils.Context;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FreeTextSearch {

    public static class SearchCriterion<T extends NumIdEntity> {

        private final String field;
        private final Function<Photo, Integer> photoGetFunction;
        private final BiFunction<Context, String, Map<T, Float>> searchFreeTextFunction;

        public SearchCriterion(String field, Function<Photo, Integer> photoGetFunction, BiFunction<Context, String, Map<T, Float>> searchFreeTextFunction) {
            this.field = field;
            this.photoGetFunction = photoGetFunction;
            this.searchFreeTextFunction = searchFreeTextFunction;
        }

        public boolean matches(T t, Photo photo) {
            return Objects.equals(t.getId(), photoGetFunction.apply(photo));
        }

        public Map<T, Float> search(Context context, String token) {
            return searchFreeTextFunction.apply(context, token);
        }

        public String getField() {
            return field;
        }
    }

    private static Map<? extends VehicleClass, Float> vehicleSeriesToClassMap(Context context, Map<? extends entities.VehicleSeries, Float> vehicleSeriesMap) {
        Map<VehicleClass, Float> vehicleClasses = new HashMap<>();
        for (VehicleSeries vehicleSeries : vehicleSeriesMap.keySet()) {
            context.getVehicleClassesModel().getByVehicleSeriesId(vehicleSeries.getId()).forEach(vc -> {
                vehicleClasses.put(vc, vehicleSeriesMap.get(vehicleSeries));
            });
        }
        return vehicleClasses;
    }

    // FIXME: Missing search by number, keywords, photographer, detected text, detected objects

    public static List<SearchCriterion> SEARCH_CRITERIA;
    static {
        SEARCH_CRITERIA = List.of(
                new SearchCriterion<>("userId", Photo::getUserId, (c, s) -> c.getUsersModel().searchFreeText(s)),
                new SearchCriterion<>("countryId", Photo::getCountryId, (c, s) -> c.getCountriesModel().searchFreeText(s)),
                new SearchCriterion<>("locationId", Photo::getLocationId, (c, s) -> c.getLocationsModel().searchFreeText(s)),
                new SearchCriterion<>("operatorId", Photo::getOperatorId, (c, s) -> c.getOperatorsModel().searchFreeText(s)),
                new SearchCriterion<>("vehicleClassId", Photo::getVehicleClassId, (c, s) -> c.getVehicleClassesModel().searchFreeText(s)),
                new SearchCriterion<>("vehicleClassId", Photo::getVehicleClassId, (c, s) -> vehicleSeriesToClassMap(c, c.getVehicleSeriesModel().searchFreeText(s))),
                new SearchCriterion<>("numId", Photo::getId, (c, s) -> c.getPhotosModel().searchFreeText(s))
        );
    }

    private static float rank(List<TokenResult> tokenResults, Photo photo) {
        float[] points = new float[1]; // the lambdas want an immutable variable, hence this array hack
        points[0] = 0.0f;
        for (TokenResult r : tokenResults) {
            for (SearchCriterion c : SEARCH_CRITERIA) {
                Map<NumIdEntity, Float> m = r.get(c);
                m.keySet().stream().filter(o -> c.matches(o, photo)).forEach(o -> points[0] += m.get(o));
            }
        }
        return points[0];
    }

    public static List<? extends Photo> search(Context context, ContextSearch search) {
        List<? extends Photo> photos = new ArrayList<>(context.getPhotosModel().searchAll(search).toList());
        Collections.sort(photos, new PhotoRankComparator(search.getFreeTextSearchTokenResults()));
        return photos;
    }

    private static class PhotoRankComparator implements Comparator<Photo> {
        private final List<TokenResult> tokenResults;

        public PhotoRankComparator(List<TokenResult> tokenResults) {
            this.tokenResults = tokenResults;
        }

        @Override
        public int compare(Photo p1, Photo p2) {
            float r1 = rank(tokenResults, p1);
            float r2 = rank(tokenResults, p2);
            if (r1 != r2) {
                return r1 < r2 ? 1 : -1;
            }
            if (p1.getAuthorRating() != p2.getAuthorRating()) {
                return p1.getAuthorRating() < p2.getAuthorRating() ? 1 : -1;
            }
            if (p1.getViews() != p2.getViews()) {
                return p1.getViews() < p2.getViews() ? 1 : -1;
            }
            return p1.getId() < p2.getId() ? 1 : -1;
        }
    }

    public static F.Tuple<Photo, Photo> getPrevNext(Context context, Photo photo, ContextSearch search) {
        List<? extends Photo> photos = search(context, search);
        int pos = photos.indexOf(photo);
        if (pos == -1) {
            return new F.Tuple<>(null, null);
        }
        Photo prev = null;
        Photo next = null;
        if (pos > 0) {
            prev = photos.get(pos - 1);
        }
        if (pos < photos.size() - 1) {
            next = photos.get(pos + 1);
        }
        return new F.Tuple<>(prev, next);
    }
}
