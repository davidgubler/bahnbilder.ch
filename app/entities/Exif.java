package entities;

import java.time.LocalDateTime;

public interface Exif {
    Integer getWidth();
    Integer getHeight();
    LocalDateTime getDateTime();
    String getCamera();
    String getDrone();
    String getFocalLength();
    String getAperture();
    String getExposure();
    String getSensitivity();
    Double getLng();
    Double getLat();
    String getKeywords();
}
