package biz;

import entities.*;
import entities.formdata.PhotoFormData;
import play.mvc.Http;
import utils.*;
import utils.geometry.SimplePoint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Photos {

    private BahnbilderLogger logger = new BahnbilderLogger(Photos.class);

    public void rate(Context context, Photo photo, Integer authorRating, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.canEdit(photo)) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateInt(authorRating, "authorRating", true, 1, 5, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getPhotosModel().rate(photo, authorRating);

        // LOG
        logger.info(context.getRequest(), user + " rated " + photo);
    }

    private Integer extractCountryIdFromKeywords(Context context, String k) {
        if (k == null) {
            return null;
        }
        String[] keywords = k.split(";");
        for (String keyword : keywords) {
            if (keyword.startsWith("country:")) {
                keyword = keyword.substring(8).trim();
                Country country = context.getCountriesModel().getByName(keyword);
                if (country != null) {
                    return country.getId();
                }
            }
        }
        for (String keyword : keywords) {
            Country country = context.getCountriesModel().getByName(keyword);
            if (country != null) {
                return country.getId();
            }
        }
        return null;
    }

    private Integer extractOperatorIdFromKeywords(Context context, String k) {
        if (k == null) {
            return null;
        }
        String[] keywords = k.split(";");
        for (String keyword : keywords) {
            if (keyword.startsWith("operator:")) {
                keyword = keyword.substring(9).trim();
                Operator operator = context.getOperatorsModel().getByName(keyword);
                if (operator != null) {
                    return operator.getId();
                }
            }
        }
        for (String keyword : keywords) {
            Operator operator = context.getOperatorsModel().getByName(keyword);
            if (operator != null) {
                return operator.getId();
            }
        }
        return null;
    }

    private Integer extractVehicleClassIdFromKeywords(Context context, String k) {
        if (k == null) {
            return null;
        }
        String[] keywords = k.split(";");
        for (String keyword : keywords) {
            if (keyword.startsWith("vclass:")) {
                keyword = keyword.substring(7).trim();
                VehicleClass vehicleClass = context.getVehicleClassesModel().getByName(keyword);
                if (vehicleClass != null) {
                    return vehicleClass.getId();
                }
            }
        }
        for (String keyword : keywords) {
            VehicleClass vehicleClass = context.getVehicleClassesModel().getByName(keyword);
            if (vehicleClass != null) {
                return vehicleClass.getId();
            }
        }
        return null;
    }

    private Integer extractNrFromKeywords(String k) {
        if (k == null) {
            return null;
        }
        String[] keywords = k.split(";");
        for (String keyword : keywords) {
            if (keyword.startsWith("nr:")) {
                keyword = keyword.substring(3).trim();
                try {
                    return Integer.parseInt(keyword);
                } catch (Exception e) {
                    // too bad
                }
            }
        }
        return null;
    }

    public Photo upload(Context context, Http.MultipartFormData.FilePart<Object> filePart, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        String fileName = filePart.getFilename();
        byte[] fileData = filePart.transformRefToBytes().toArray();
        if (fileData.length > Config.MAX_PHOTO_SIZE) {
            throw new ValidationException(Map.of(fileName, ErrorMessages.PHOTO_TOO_LARGE));
        }
        if (!"image/jpeg".equals(filePart.getContentType())) {
            throw new ValidationException(Map.of(fileName, ErrorMessages.PHOTO_WRONG_FORMAT));
        }
        Exif exif = context.getPhotosModel().extractExif(fileData);

        // BUSINESS
        int photoTypeId = exif.getDrone() == null ? Config.DEFAULT_PHOTO_TYPE_ID : Config.DRONE_PHOTO_TYPE_ID;
        Double lng = exif.getLng();
        Double lat = exif.getLat();
        Integer countryId = extractCountryIdFromKeywords(context, exif.getKeywords());
        if (countryId == null && lat != null && lng != null) {
            try {
                Country country = context.getGeocodingModel().getCountryByPoint(new SimplePoint(lat, lng)).get();
                countryId = country == null ? null : country.getId();
            } catch (Exception e) {
                logger.error(context.getRequest(), e);
            }
        }
        Integer operatorId = extractOperatorIdFromKeywords(context, exif.getKeywords());
        Integer vehicleClassId = extractVehicleClassIdFromKeywords(context, exif.getKeywords());
        Integer nr = extractNrFromKeywords(exif.getKeywords());
        Photo photo = context.getPhotosModel().create(exif, user.getId(), fileName, Instant.now(), exif.getDateTime(), user.getDefaultLicenseId(), photoTypeId, countryId, lng, lat, operatorId, vehicleClassId, nr);
        context.getFilesOriginalModel().create(photo.getId(), fileData);

        // LOG
        logger.info(context.getRequest(), user + " uploaded " + photo);
        return photo;
    }

    public Photo replace(Context context, Photo photo, Http.MultipartFormData.FilePart<Object> filePart, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.canEdit(photo)) {
            throw new NotAllowedException();
        }

        // INPUT
        byte[] fileData = filePart.transformRefToBytes().toArray();
        if (fileData.length > Config.MAX_PHOTO_SIZE) {
            throw new ValidationException(Map.of("file", ErrorMessages.PHOTO_TOO_LARGE));
        }
        if (!"image/jpeg".equals(filePart.getContentType())) {
            throw new ValidationException(Map.of("file", ErrorMessages.PHOTO_WRONG_FORMAT));
        }
        Exif exif = context.getPhotosModel().extractExif(fileData);

        // BUSINESS
        context.getPhotosModel().update(photo, exif);
        context.getFilesOriginalModel().update(photo.getId(), fileData);

        // LOG
        logger.info(context.getRequest(), user + " replaced " + photo);
        return photo;
    }

    public void update(Context context, PhotoFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }
        data.photos.forEach(p -> {if (!user.canEdit(p)) {throw new NotAllowedException();}});

        // INPUT
        Map<String, String> errors = new HashMap<>();
        LocalDate date = InputUtils.validateDate(data.date, "date", false, errors);
        LocalTime time = InputUtils.validateTime(data.time, "time", false, errors);
        if (date == null && time != null) {
            errors.put("date", ErrorMessages.MUST_SET_DATE_AND_TIME);
        }
        if (time == null && date != null) {
            errors.put("time", ErrorMessages.MUST_SET_DATE_AND_TIME);
        }
        LocalDateTime dateTime = (date != null && time != null) ? LocalDateTime.of(date, time) : null;
        List<String> oldLabels = InputUtils.toListOfStrings(data.labelsBefore, ",");
        if (oldLabels == null) {
            oldLabels = Collections.emptyList();
        }
        List<String> newLabels = InputUtils.toListOfStrings(data.labels, ",");
        if (newLabels == null) {
            newLabels = Collections.emptyList();
        }
        Set<String> labelsToAdd = new HashSet<>(newLabels);
        labelsToAdd.removeAll(oldLabels);
        Set<String> labelsToRemove = new HashSet<>(oldLabels);
        labelsToRemove.removeAll(newLabels);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        Integer locationId = data.locationId;
        if (data.newLocation != null) {
            Location location = context.getLocationsModel().getByName(data.newLocation);
            if (location == null) {
                location = context.getLocationsModel().create(data.newLocation);
            }
            locationId = location.getId();
        }
        context.getPhotosModel().update(data, dateTime, locationId, labelsToAdd, labelsToRemove);

        // LOG
        logger.info(context.getRequest(), user + " updated " + data.photos);
    }

    // used for the autodetection feature
    public void update(Context context, Photo photo, Operator operator, VehicleClass vehicleClass, Integer nr, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.canEdit(photo)) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        if (operator == null) {
            errors.put("operator", ErrorMessages.MISSING_VALUE);
        }
        if (vehicleClass == null) {
            errors.put("vehicleClass", ErrorMessages.MISSING_VALUE);
        }
        if (vehicleClass == null) {
            errors.put("nr", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getPhotosModel().update(photo, operator, vehicleClass, nr);

        // LOG
        logger.info(context.getRequest(), user + " updated " + photo);
    }

    public void delete(Context context, PhotoFormData data, User user) {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }
        data.photos.forEach(p -> {if (!user.canEdit(p)) {throw new NotAllowedException();}});

        // INPUT
        List<Integer> photoIds = data.photos.stream().map(Photo::getId).collect(Collectors.toUnmodifiableList());


        // BUSINESS
        context.getFilesScaledModel().delete(photoIds);
        context.getFilesOriginalModel().delete(photoIds);
        context.getPhotosModel().delete(data);

        // LOG
        logger.info(context.getRequest(), user + " deleted " + data.photos);
    }
}
