package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.ModelSearch;
import entities.Photo;
import entities.User;
import i18n.Lang;
import i18n.Txt;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Config;
import utils.Context;
import utils.Json;
import utils.geometry.NearbyMap;
import utils.geometry.Point;
import java.util.ArrayList;
import java.util.List;

public class MapController extends Controller {

    @Inject
    private Injector injector;

    public Result view(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        ModelSearch search = new ModelSearch(request);
        if (!search.isActive()) {
            search = new ModelSearch(2);
        }
        injector.injectMembers(search);


        return ok(views.html.map.view.render(request, search, Config.Option.GOOGLE_MAPS_JS_KEY.get(), user, lang));
    }

    public Result markers(Http.Request request) {
        Context context = Context.get(request);
        ModelSearch search = new ModelSearch(request);
        injector.injectMembers(search);
        NearbyMap<Photo> nearbyMap = new NearbyMap<>(Config.PHOTO_SPOT_RADIUS_KM);
        context.getPhotosModel().getCoordinates(search).forEach(p -> {
            Point photoCoordinates = p.getCoordinates();
            Photo nearbyPhoto = nearbyMap.getNearest(photoCoordinates);
            if (nearbyPhoto == null) {
                nearbyMap.put(photoCoordinates, p);
            }
        });
        List<Marker> markers = new ArrayList<>();
        nearbyMap.keySet().forEach(p -> markers.add(new Marker(p, nearbyMap.get(p))));
        try {
            return ok("var markerData = " + Json.MAPPER.writeValueAsString(markers) + ";").as(Http.MimeTypes.JAVASCRIPT);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Result config(Http.Request request) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        return ok("var txtMore = \"" + Txt.get(lang, "more") + "\";\nvar mapsKey = \"" + Config.Option.GOOGLE_MAPS_JS_KEY.get() + "\";").as(Http.MimeTypes.JAVASCRIPT);
    }

    private static class Marker {
        private final double lng;
        private final double lat;
        private final Integer photoId;
        public Marker(Point p, Photo photo) {
            lng = p.getLng();
            lat = p.getLat();
            photoId = photo.getId();
        }
    }
}
