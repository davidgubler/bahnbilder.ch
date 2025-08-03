package entities;

import java.time.Instant;

public interface File {
    int getPhotoId();
    PhotoResolution.Size getSize();
    Instant getLastModified();
    String getEtag();
    byte[] getData();
    PhotoResolution getResolution();
}
