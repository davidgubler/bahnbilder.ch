package controllers;

import biz.Autodetection;
import biz.Photos;
import biz.ValidationException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import entities.*;
import entities.formdata.PhotoFormData;
import entities.tmp.AutodetectionStatus;
import entities.tmp.TmpLocation;
import utils.*;
import utils.geometry.GeographicCoordinates;
import utils.geometry.Point;
import i18n.Lang;
import i18n.Txt;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.HtmlFormat;
import services.Railinfo;
import utils.geometry.SimplePoint;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class PhotoController extends Controller {

    @Inject
    private Photos photos;

    @Inject
    private Railinfo railinfo;

    @Inject
    private Autodetection autodetection;

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Result viewRedirect(Http.Request request, Integer id) {
        return redirect("/" + id);
    }

    public Result view(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Photo photo = context.getPhotosModel().get(id);
        if (photo == null) {
            throw new NotFoundException("Photo");
        }
        String menu = InputUtils.trimToNull(request.queryString("menu").orElse(""));
        context.getViewsModel().countView(photo.getId(), request);
        return ok(views.html.photos.view.render(request, photo, menu, user, lang));
    }

    public Result state(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Photo photo = context.getPhotosModel().get(id);
        if (photo == null) {
            throw new NotFoundException("Photo");
        }

        Search search = new Search(request);

        StateInfo state = new StateInfo();
        state.photo = new PhotoInfo();
        state.photo.id = photo.getId();
        state.photo.path = "/" + photo.getId();
        state.photo.url = "/" + photo.getId();
        state.photo.label = photo.getGeneratedDescription(lang, true, true, true, true, true);
        state.photo.title = photo.getGeneratedDescription(lang, true, false, true, true, false);
        state.photo.photographer = photo.getPhotographer();
        state.photo.authorRating = photo.getAuthorRating();
        state.photo.nr = photo.getNr();
        state.photo.camera = photo.getExif().getCamera();
        state.photo.focalLength = photo.getExif().getFocalLength();
        state.photo.exposure = photo.getExif().getExposure();
        state.photo.aperture = photo.getExif().getAperture();
        state.photo.sensitivity = photo.getExif().getSensitivity();
        state.photo.longitude = photo.getLng();
        state.photo.latitude = photo.getLat();
        state.photo.viewsUncollected = photo.getViewsUncollected();
        state.photo.srcset = photo.getSrcSet();
        state.photo.canEdit = user != null && user.canEdit(photo);
        state.photo.bbCodeSmall = "[url=" + Config.getSelfUrl(request) + "/" + state.photo.id + "][img]" + photo.getUrlPublic900() + "[/img][/url]";
        state.photo.bbCodeLarge = "[url=" + Config.getSelfUrl(request) + "/" + state.photo.id + "][img]" + photo.getUrlPublic1280() + "[/img][/url]";
        state.photo.download = new DownloadInfo(views.html.photos.downloadLinks.render(photo).toString());
        state.photo.width = photo.getResXLarge().getWidth();
        state.photo.height = photo.getResXLarge().getHeight();

        Photo next = context.getPhotosModel().getNext(photo, search);
        state.next = next == null ? null : next.getId();

        Photo prev = context.getPhotosModel().getPrev(photo, search);
        state.prev = prev == null ? null : prev.getId();

        if (photo.getDescription() != null) {
            state.photo.description = new DescriptionInfo(photo.getDescription(), HtmlFormat.escape(photo.getDescription()).toString());
        }
        if (photo.getVehicleClass() != null) {
            state.photo.vehicleClass = new VehicleClassInfo(photo.getVehicleClass().getId(), photo.getVehicleClass().getName(), views.html.photos.vehicleClassLink.render(photo).toString());
            if (photo.getVehicleClass().getVehicleSeries() != null) {
                state.photo.vehicleClass.vehicleSeries = new VehicleSeriesInfo(photo.getVehicleClass().getVehicleSeries().getId(), photo.getVehicleClass().getVehicleSeries().getName(), views.html.photos.vehicleSeriesLink.render(photo).toString());
            }
        }
        if (photo.getOperator() != null) {
            state.photo.operator = new OperatorInfo(photo.getOperator().getId(), photo.getOperator().getName(), views.html.photos.operatorLink.render(photo).toString());
        }
        if (photo.getUser() != null) {
            if (photo.getPhotographer() == null) {
                state.photo.author = new AuthorInfo(photo.getUser().getId(), photo.getUser().getName());
            } else {
                state.photo.collection = new AuthorInfo(photo.getUser().getId(), photo.getUser().getName());
            }
        }
        if (photo.getPhotoDate() != null) {
            state.photo.photoDate = new PhotoDateInfo(views.html.photos.dateTimeLink.render(photo, lang).toString());
        }
        if (photo.getLicense() != null) {
            state.photo.license = new LicenseInfo(photo.getLicense().getId(), photo.getLicense().getLogo(lang));
        }

        try {
            context.getViewsModel().countView(photo.getId(), request);
            return ok(MAPPER.writeValueAsString(state)).as(Http.MimeTypes.JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Result rate(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        Photo photo = context.getPhotosModel().get(id);
        if (photo == null) {
            throw new NotFoundException("Photo");
        }

        Map<String, String[]> data = request.body().asFormUrlEncoded();
        Integer authorRating = InputUtils.toInt(data.get("authorRating"));
        try {
            photos.rate(context, photo, authorRating, user);
        } catch (ValidationException e) {
            return badRequest();
        }

        return ok();
    }

    public Result edit(Http.Request request, String ids, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted(Comparator.comparing(Photo::getId)).toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        for (Photo photo : photos) {
            if (!user.canEdit(photo)) {
                throw new NotAllowedException();
            }
        }

        PhotoFormData data = new PhotoFormData(request, returnUrl, photos);
        List<? extends License> licenses = context.getLicensesModel().getAll();
        List<? extends PhotoType> photoTypes = context.getPhotoTypesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
        List<? extends Country> countries = context.getCountriesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
        List<? extends Location> locations = context.getLocationsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
        List<? extends Operator> operators = context.getOperatorsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
        List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
        return ok(views.html.photos.edit.render(request, data, licenses, photoTypes, countries, locations, operators, vehicleClasses, Collections.emptyMap(), Config.Option.GOOGLE_MAPS_JS_KEY.get(), user));
    }

    public Result editPost(Http.Request request, String ids, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted(Comparator.comparing(Photo::getId)).toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        PhotoFormData data = new PhotoFormData(request, returnUrl, photos);
        try {
            this.photos.update(context, data, user);
        } catch (ValidationException e) {
            List<? extends License> licenses = context.getLicensesModel().getAll();
            List<? extends PhotoType> photoTypes = context.getPhotoTypesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
            List<? extends Country> countries = context.getCountriesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
            List<? extends Location> locations = context.getLocationsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
            List<? extends Operator> operators = context.getOperatorsModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
            List<? extends VehicleClass> vehicleClasses = context.getVehicleClassesModel().getAll().sorted(LocalizedComparator.get(data.lang)).toList();
            return ok(views.html.photos.edit.render(request, data, licenses, photoTypes, countries, locations, operators, vehicleClasses, e.getErrors(), Config.Option.GOOGLE_MAPS_JS_KEY.get(), user));
        }
        return redirect(data.returnUrl);
    }

    public Result delete(Http.Request request, String ids, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted().toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        for (Photo photo : photos) {
            if (!user.canEdit(photo)) {
                throw new NotAllowedException();
            }
        }
        PhotoFormData data = new PhotoFormData(request, returnUrl, photos);
        return ok(views.html.photos.delete.render(request, data, user));
    }

    public Result deletePost(Http.Request request, String ids, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted().toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        PhotoFormData data = new PhotoFormData(request, returnUrl, photos);
        this.photos.delete(context, data, user);
        return redirect(returnUrl);
    }

    public Result replace(Http.Request request, Integer id, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        Photo photo = context.getPhotosModel().get(id);
        String lang = Lang.get(request);
        if (user == null || !user.canEdit(photo)) {
            throw new NotAllowedException();
        }
        return ok(views.html.photos.replace.render(request, photo, returnUrl, Map.of("file", "adsfa"), user, lang));
    }

    public Result replacePost(Http.Request request, Integer id, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        Photo photo = context.getPhotosModel().get(id);
        String lang = Lang.get(request);

        Http.MultipartFormData<Object> data = request.body().asMultipartFormData();
        for (Http.MultipartFormData.FilePart<Object> filePart : data.getFiles()) {
            try {
                photos.replace(context, photo, filePart, user);
            } catch (ValidationException e) {
                return ok(views.html.photos.replace.render(request, photo, returnUrl, e.getErrors(), user, lang));
            }
            break;
        }
        return redirect(returnUrl);
    }

    public Result autodetect(Http.Request request, String ids, String returnUrl) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted(Comparator.comparing(Photo::getId)).toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        for (Photo photo : photos) {
            if (!user.canEdit(photo)) {
                throw new NotAllowedException();
            }
        }
        PhotoFormData data = new PhotoFormData(request, returnUrl, photos);

        Map<Photo, AutodetectionStatus> autodetectionStatusMap = new HashMap<>();
        for (Photo photo : photos) {
            autodetectionStatusMap.put(photo, autodetection.autodetect(context, photo));
        }

        return ok(views.html.photos.autodetect.render(request, data, autodetectionStatusMap, user, lang));
    }

    public Result autodetectPost(Http.Request request, String ids, String returnUrl) throws ValidationException {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photoList = context.getPhotosModel().getByIds(photoIds).toList();
        if (photoList.isEmpty()) {
            throw new NotFoundException("Photos");
        }
        Map<String, String[]> formData = request.body().asFormUrlEncoded();
        for (Photo photo : photoList) {
            try {
                String[] solution = InputUtils.trimToNull(formData.get("" + photo.getId())).split("\\|");
                if (solution.length != 3) {
                    continue;
                }
                Operator operator = context.getOperatorsModel().get(Integer.parseInt(solution[0]));
                VehicleClass vehicleClass = context.getVehicleClassesModel().get(Integer.parseInt(solution[1]));
                Integer nr = Integer.parseInt(solution[2]);
                photos.update(context, photo, operator, vehicleClass, nr, user);
            } catch (Exception e) {
                BahnbilderLogger.error(request, e);
            }
        }
        return redirect(returnUrl);
    }

    private List<Location> suggestLocations(Context context, Point point) {
        List<Station> stations = context.getGeocodingModel().getNearbyStations(point);
        List<Station> curatedList = new ArrayList<>(stations);
        for (int i = 0; i < stations.size(); i++) {
            double bearing = GeographicCoordinates.bearingDegrees(point, stations.get(i));
            for (int j = i+1; j < stations.size(); j++) {
                Station stationUnderScrutiny = stations.get(j);
                if (!curatedList.contains(stationUnderScrutiny)) {
                    continue;
                }
                double b = GeographicCoordinates.bearingDegrees(point, stationUnderScrutiny);
                double angle = Math.abs((((bearing - b) + 180) % 360 + 360) % 360 - 180);
                if (angle < 60) {
                    curatedList.remove(stations.get(j));
                }
            }
        }

        if (curatedList.size() > 0 && GeographicCoordinates.distanceKm(point, curatedList.get(0)) < 0.4) {
            return List.of(new TmpLocation(curatedList.get(0).getName()));
        }
        if (curatedList.size() > 1) {
            return List.of(new TmpLocation(curatedList.get(0).getName() + " - " + curatedList.get(1).getName()), new TmpLocation(curatedList.get(1).getName() + " - " + curatedList.get(0).getName()));
        }
        return Collections.emptyList();
    }

    public Result suggestLocations(Http.Request request, String ids) throws Exception {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted(Comparator.comparing(Photo::getId)).toList();
        if (photos.isEmpty()) {
            throw new NotFoundException("Photos");
        }

        for (Photo photo : photos) {
            if (photo.getLat() != null && photo.getLng() != null) {
                Set<Location> locationSet = new HashSet<>();
                context.getPhotosModel().search(new Search(null, photo.getCoordinates(), Config.PHOTO_SPOT_RADIUS_KM)).stream().filter(p -> p.getLocation() != null).forEach(p -> locationSet.add(p.getLocation()));
                if (!locationSet.isEmpty()) {
                    return ok(MAPPER.writeValueAsString(locationSet.stream().sorted(LocalizedComparator.get(lang)).collect(Collectors.toUnmodifiableList()))).as(Http.MimeTypes.JSON);
                }

                try {
                    return ok(MAPPER.writeValueAsString(suggestLocations(context, new SimplePoint(photo.getLat(), photo.getLng())))).as(Http.MimeTypes.JSON);
                } catch (Exception e) {
                    BahnbilderLogger.error(request, e);
                    ok("[]").as(Http.MimeTypes.JSON);
                }
            }
        }
        return ok("[]").as(Http.MimeTypes.JSON);
    }

    public Result suggestTrains(Http.Request request, String ids) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        List<Integer> photoIds = InputUtils.toListOfIntegers(ids, ",");
        List<? extends Photo> photos = context.getPhotosModel().getByIds(photoIds).sorted(Comparator.comparing(Photo::getId)).toList();

        for (Photo photo : photos) {
            if (photo.getCountry() != null && photo.getLat() != null && photo.getLng() != null && photo.getPhotoDate() != null) {
                try {
                    String countryCode = photo.getCountry().getCode();
                    Point point = new SimplePoint(photo.getLat(), photo.getLng());
                    LocalDate date = photo.getPhotoDate().toLocalDate();
                    LocalTime time = photo.getPhotoDate().toLocalTime();
                    return ok(MAPPER.writeValueAsString(railinfo.guessTheTrain(countryCode, point, date, time).get())).as(Http.MimeTypes.JSON);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ok("[]").as(Http.MimeTypes.JSON);
    }

    public Result viewConfig(Http.Request request, Integer id) {
        return ok("var mapsKey = \"" + Config.Option.GOOGLE_MAPS_JS_KEY.get() + "\";\n").as(Http.MimeTypes.JAVASCRIPT);
    }

    public Result editConfig(Http.Request request, String ids) {
        String lang = Lang.get(request);
        return ok("var txtSuggestions = \"" + Txt.get(lang, "suggestions") + "\";\n").as(Http.MimeTypes.JAVASCRIPT);
    }

    private static class DescriptionInfo {
        DescriptionInfo(String text, String html) {
            this.text = text;
            this.html = html;
        }
        String text;
        String html;
    }

    private static class DownloadInfo {
        DownloadInfo(String html) {
            this.html = html;
        }
        String html;
    }

    private static class LicenseInfo {
        LicenseInfo(int id, String html) {
            this.id = id;
            this.html = html;
        }
        int id;
        String html;
    }

    private static class PhotoDateInfo {
        PhotoDateInfo(String html) {
            this.html = html;
        }
        String html;
    }

    private static class AuthorInfo {
        AuthorInfo(int id, String name) {
            this.id = id;
            this.name = name;
        }
        int id;
        String name;
    }

    private static class OperatorInfo {
        OperatorInfo(int id, String name, String html) {
            this.id = id;
            this.name = name;
            this.html = html;
        }
        int id;
        String name;
        String html;
    }

    private static class VehicleSeriesInfo {
        VehicleSeriesInfo(int id, String name, String html) {
            this.id = id;
            this.name = name;
            this.html = html;
        }
        int id;
        String name;
        String html;
    }

    private static class VehicleClassInfo {
        VehicleClassInfo(int id, String name, String html) {
            this.id = id;
            this.name = name;
            this.html = html;
        }
        int id;
        String name;
        String html;
        VehicleSeriesInfo vehicleSeries;
    }

    private static class PhotoInfo {
        int id;
        String url;
        String path;
        String label;
        String title;
        String photographer;
        DescriptionInfo description;
        VehicleClassInfo vehicleClass;
        Integer nr;
        OperatorInfo operator;
        AuthorInfo author;
        AuthorInfo collection;
        int authorRating;
        PhotoDateInfo photoDate;
        LicenseInfo license;
        String camera;
        String focalLength;
        String exposure;
        String aperture;
        String sensitivity;
        Double longitude;
        Double latitude;
        String bbCodeLarge;
        String bbCodeSmall;
        int viewsUncollected;
        String srcset;
        boolean canEdit;
        DownloadInfo download;
        Integer width;
        Integer height;
    }

    private static class StateInfo {
        PhotoInfo photo;
        Integer next;
        Integer prev;
    }
}
