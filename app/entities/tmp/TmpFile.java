package entities.tmp;

import entities.File;
import entities.PhotoResolution;

import java.time.Instant;

public class TmpFile implements File {
    public enum Status { NEW, UNMODIFIED, MODIFIED, DELETED };

    private byte[] data;

    private Instant lastModified;

    private String etag;

    public TmpFile(byte[] data, Instant lastModified, String etag) {
        this.data = data;
        this.lastModified = lastModified;
        this.etag = etag;
    }

    @Override
    public int getPhotoId() {
        throw new IllegalStateException();
    }

    @Override
    public PhotoResolution.Size getSize() {
        throw new IllegalStateException();
    }

    @Override
    public Instant getLastModified() {
        return lastModified;
    }

    @Override
    public String getEtag() {
        return etag;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public PhotoResolution getResolution() {
        throw new IllegalStateException();
    }
}
