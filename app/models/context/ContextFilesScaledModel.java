package models.context;

import com.google.inject.Inject;
import entities.File;
import entities.Photo;
import entities.PhotoResolution;
import models.FilesScaledModel;
import utils.Context;

import java.time.Instant;
import java.util.List;

public class ContextFilesScaledModel extends ContextModel implements FilesScaledModel {

    @Inject
    private FilesScaledModel filesScaledModel;

    public ContextFilesScaledModel(Context context) {
        this.context = context;
    }

    @Override
    public String getPublic(Photo photo, PhotoResolution.Size size) {
        return call(() -> filesScaledModel.getPublic(photo, size));
    }

    @Override
    public File get(Photo photo, PhotoResolution.Size size, Instant ifModifiedSince, String ifNoneMatch) {
        return call(() -> filesScaledModel.get(photo, size, ifModifiedSince, ifNoneMatch));
    }

    @Override
    public void delete(List<Integer> photoIds) {
        call(() -> { filesScaledModel.delete(photoIds); return null; });
    }
}
