package entities.formdata;

import entities.LocalizedComparator;
import entities.Photo;
import play.mvc.Http;
import utils.InputUtils;
import utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PhotoFormData extends FormData<Photo> {
    public final List<? extends Photo> photos;
    public final String ids;
    public final String date;
    public final String time;
    public final String photographer;
    public final Integer licenseId;
    public final Integer photoTypeId;
    public final Integer countryId;
    public final Integer locationId;
    public final String newLocation;
    public final Integer operatorId;
    public final Integer vehicleClassId;
    public final Integer nr;
    public final String description;
    public final String labels;
    public final Double lat;
    public final Double lng;

    public PhotoFormData(Http.Request request, String returnUrl, List<? extends Photo> photos) {
        super(request, returnUrl, photos.get(0));
        this.photos = photos;
        this.ids = StringUtils.join(photos.stream().map(Photo::getId).collect(Collectors.toUnmodifiableList()), ",");
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            date = InputUtils.trimToNull(data.get("date"));
            time = InputUtils.trimToNull(data.get("time"));
            photographer = InputUtils.trimToNull(data.get("photographer"));
            licenseId = InputUtils.toInt(data.get("license"));
            photoTypeId = InputUtils.toInt(data.get("photoType"));
            countryId = InputUtils.toInt(data.get("country"));
            locationId = InputUtils.toInt(data.get("location"));
            newLocation = InputUtils.trimToNull(data.get("newLocation"));
            operatorId = InputUtils.toInt(data.get("operator"));
            vehicleClassId = InputUtils.toInt(data.get("vehicleClass"));
            nr = InputUtils.toInt(data.get("nr"));
            description = InputUtils.trimToNull(data.get("description"));
            labels = InputUtils.trimToNull(data.get("labels"));
            lat = InputUtils.toDouble(data.get("lat"));
            lng = InputUtils.toDouble(data.get("lng"));
        } else {
            if (photos.size() == 1) {
                date = StringUtils.formatDate(lang, photos.get(0).getPhotoDate());
                time = StringUtils.formatTime(lang, photos.get(0).getPhotoDate());
                photographer = photos.get(0).getPhotographer();
                licenseId = photos.get(0).getLicenseId();
                photoTypeId = photos.get(0).getPhotoTypeId();
                countryId = photos.get(0).getCountryId();
                locationId = photos.get(0).getLocationId();
                newLocation = null;
                operatorId = photos.get(0).getOperatorId();
                vehicleClassId = photos.get(0).getVehicleClassId();
                nr = photos.get(0).getNr();
                description = photos.get(0).getDescription();
                labels = StringUtils.join(photos.get(0).getLabels(), ",");
                lat = photos.get(0).getLat();
                lng = photos.get(0).getLng();
            } else {
                date = null;
                time = null;
                photographer = null;
                licenseId = null;
                photoTypeId = null;
                countryId = null;
                locationId = null;
                newLocation = null;
                operatorId = null;
                vehicleClassId = null;
                nr = null;
                description = null;
                Set<String> labelsSet = new HashSet<>();
                photos.forEach(p -> labelsSet.addAll(p.getLabels()));
                labels = StringUtils.join(labelsSet.stream().sorted().collect(Collectors.toUnmodifiableList()), ",");
                lat = null;
                lng = null;
            }
        }
    }

    public PhotoFormData(Http.Request request, String returnUrl, List<? extends Photo> photos, Integer operatorId, Integer vehicleClassId, Integer nr) {
        super(request, returnUrl, photos.get(0));
        this.photos = photos;
        ids = null;
        date = null;
        time = null;
        photographer = null;
        licenseId = null;
        photoTypeId = null;
        countryId = null;
        locationId = null;
        newLocation = null;
        this.operatorId = operatorId;
        this.vehicleClassId = vehicleClassId;
        this.nr = nr;
        description = null;
        labels = null;
        lat = null;
        lng = null;
    }
}
