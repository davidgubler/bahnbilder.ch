package models;

import entities.File;
import entities.Photo;
import entities.PhotoResolution;

import java.time.Instant;
import java.util.List;

public interface FilesScaledModel {
    String getPublic(Photo photo, PhotoResolution.Size size);

    File get(Photo photo, PhotoResolution.Size size, Instant ifModifiedSince, String ifNoneMatch);

    void delete(List<Integer> photoIds);
}
