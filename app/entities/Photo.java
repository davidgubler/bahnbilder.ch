package entities;

import i18n.PhotoDescriptionGenerator;
import utils.geometry.Point;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface Photo {
    int getId();

    int getUserId();

    User getUser();

    String getPhotographer();

    Exif getExif();

    LocalDateTime getPhotoDate();

    Instant getUploadDate();

    String getUploadFilename();

    Integer getPhotoTypeId();

    PhotoType getPhotoType();

    Integer getLocationId();

    Location getLocation();

    Integer getCountryId();

    Country getCountry();

    Integer getOperatorId();

    Operator getOperator();

    Integer getVehicleClassId();

    VehicleClass getVehicleClass();

    Integer getNr();

    String getDescription();

    default String getGeneratedDescription(String lang, boolean inclVehicleClass, boolean inclOperator, boolean inclOperatorAbbr, boolean inclLocation, boolean inclCountry) {
        return PhotoDescriptionGenerator.getCustomDescription(lang, this, inclVehicleClass, inclOperator, inclOperatorAbbr, inclLocation, inclCountry);
    }

    Integer getLicenseId();

    License getLicense();

    int getViews();

    int getViewsUncollected();

    int getAuthorRating();

    Double getLng();

    Double getLat();

    Point getCoordinates();

    List<String> getTexts();

    List<String> getLabels();

    List<? extends PhotoResolution> getResolutions();

    default PhotoResolution getResSmall() {
        return ((List<PhotoResolution>)getResolutions()).stream().filter(r -> "small".equals(r.getName())).findFirst().orElse(getResOriginal());
    }

    default PhotoResolution getResMedium() {
        return ((List<PhotoResolution>)getResolutions()).stream().filter(r -> "medium".equals(r.getName())).findFirst().orElse(getResOriginal());
    }

    default PhotoResolution getResLarge() {
        return ((List<PhotoResolution>)getResolutions()).stream().filter(r -> "large".equals(r.getName())).findFirst().orElse(getResOriginal());
    }

    default PhotoResolution getResXLarge() {
        return ((List<PhotoResolution>)getResolutions()).stream().filter(r -> "xlarge".equals(r.getName())).findFirst().orElse(getResOriginal());
    }

    default PhotoResolution getResOriginal() {
        return ((List<PhotoResolution>)getResolutions()).stream().filter(r -> "original".equals(r.getName())).findFirst().get();
    }

    default String getSrcSet() {
        return getResolutions().stream().filter(r -> r.getWidth() >= PhotoResolution.Size.medium.getMaxWidth() && r.getWidth() <= PhotoResolution.Size.xxlarge.getMaxWidth()).map(r -> getUrl(r) + " " + r.getWidth() + "w").collect(Collectors.joining(", "));
    }

    String getUrl(PhotoResolution resolution);

    String getUrlOriginal();

    String getUrlPublic200();

    String getUrlPublic900();

    String getUrlPublic1280();

    String getUrlPublic1600();

    String getUrlPublic2400();
}
