package entities.mongodb;

import dev.morphia.annotations.Entity;
import entities.Exif;

import java.time.LocalDateTime;

@Entity(useDiscriminator = false)
public class MongoDbPhotoExif implements Exif {
    private Integer width;
    private Integer height;
    private LocalDateTime dateTime;
    private String camera;
    private String focalLength;
    private String aperture;
    private String exposure;
    private String sensitivity;
    private Double lng;
    private Double lat;
    private String keywords;

    public MongoDbPhotoExif() {
        // dummy for Morphia
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String getCamera() {
        if (getDrone() != null) {
            return getDrone();
        }
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    @Override
    public String getDrone() {
        if ("L1D-20c".equals(camera)) {
            return "DJI Mavic 2 Pro";
        }
        if ("FC220".equals(camera)) {
            return "DJI Mavic Pro";
        }
        if ("FC3411".equals(camera)) {
            return "DJI Air 2S";
        }
        if ("FC8482".equals(camera)) {
            return "DJI Mini 4 Pro";
        }
        if ("L2D-20c".equals(camera)) {
            return "DJI Mavic 3 Pro";
        }
        return null;
    }

    @Override
    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    @Override
    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    @Override
    public String getExposure() {
        return exposure;
    }

    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    @Override
    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    @Override
    public Double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public Double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
