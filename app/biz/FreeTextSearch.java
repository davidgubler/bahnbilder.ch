package biz;

import entities.*;
import entities.search.ContextSearch;
import entities.search.TokenResult;
import play.libs.F;
import utils.Context;

import java.util.*;

public class FreeTextSearch {

    // FIXME: Missing search by number, keywords, photographer, detected text, detected objects

    private static float rank(List<TokenResult> tokenResults, Photo photo) {
        float[] points = new float[1]; // the lambdas want an immutable variable, hence this array hack
        points[0] = 0.0f;

        for (TokenResult r : tokenResults) {
            r.getUsers().keySet().stream().filter(u -> Objects.equals(u.getId(), photo.getUserId())).forEach(u -> points[0] += r.getUsers().get(u));
            r.getCountries().keySet().stream().filter(c -> Objects.equals(c.getId(), photo.getCountryId())).forEach(c -> points[0] += r.getCountries().get(c));
            r.getLocations().keySet().stream().filter(l -> Objects.equals(l.getId(), photo.getLocationId())).forEach(l -> points[0] += r.getLocations().get(l));
            r.getOperators().keySet().stream().filter(o -> Objects.equals(o.getId(), photo.getOperatorId())).forEach(o -> points[0] += r.getOperators().get(o));
            r.getVehicleClasses().keySet().stream().filter(v -> Objects.equals(v.getId(), photo.getVehicleClassId())).forEach(v -> points[0] += r.getVehicleClasses().get(v));
            r.getVehicleClassesBySeries().keySet().stream().filter(v -> Objects.equals(v.getId(), photo.getVehicleClassId())).forEach(v -> points[0] += r.getVehicleClassesBySeries().get(v));
            r.getPhotosByDescription().keySet().stream().filter(p -> Objects.equals(p.getId(), photo.getId())).forEach(p -> points[0] += r.getPhotosByDescription().get(p));
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
