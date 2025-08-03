package models;

import entities.File;
import entities.Photo;
import entities.mongodb.MongoDbFile;
import entities.tmp.TmpFile;

import java.time.Instant;
import java.util.List;

public interface FilesOriginalModel {

    String getPublic(Photo photo);

    MongoDbFile create(int photoId, byte[] data);

    MongoDbFile create(int photoId, File file);

    void update(int photoId, byte[] data);

    void update(int photoId, File tmpFile);

    File get(Photo photo);

    File get(Photo photo, Instant ifModifiedSince, String ifNoneMatch);

    TmpFile.Status ensureMigrated(Photo photo);

    void delete(List<Integer> photoIds);
}
