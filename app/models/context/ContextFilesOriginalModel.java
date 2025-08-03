package models.context;

import com.google.inject.Inject;
import entities.File;
import entities.Photo;
import entities.mongodb.MongoDbFile;
import entities.tmp.TmpFile;
import models.FilesOriginalModel;
import utils.Context;

import java.time.Instant;
import java.util.List;

public class ContextFilesOriginalModel extends ContextModel implements FilesOriginalModel {

    @Inject
    private FilesOriginalModel filesOriginalModel;

    public ContextFilesOriginalModel(Context context) {
        this.context = context;
    }

    @Override
    public String getPublic(Photo photo) {
        return call(() -> filesOriginalModel.getPublic(photo));
    }

    @Override
    public MongoDbFile create(int photoId, byte[] data) {
        return call(() -> filesOriginalModel.create(photoId, data));
    }

    @Override
    public MongoDbFile create(int photoId, File file) {
        return call(() -> filesOriginalModel.create(photoId, file));
    }

    @Override
    public void update(int photoId, byte[] data) {
        call(() -> { filesOriginalModel.update(photoId, data); return null; });
    }

    @Override
    public void update(int photoId, File tmpFile) {
        call(() -> {filesOriginalModel.update(photoId, tmpFile); return null; });
    }

    @Override
    public File get(Photo photo) {
        return call(() -> filesOriginalModel.get(photo));
    }

    @Override
    public File get(Photo photo, Instant ifModifiedSince, String ifNoneMatch) {
        return call(() -> filesOriginalModel.get(photo, ifModifiedSince, ifNoneMatch));
    }

    @Override
    public TmpFile.Status ensureMigrated(Photo photo) {
        return call(() -> filesOriginalModel.ensureMigrated(photo));
    }

    @Override
    public void delete(List<Integer> photoIds) {
        call(() -> { filesOriginalModel.delete(photoIds); return null; });
    }
}
