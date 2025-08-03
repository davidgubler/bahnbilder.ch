package models.context;

import com.google.inject.Inject;
import entities.Photo;
import models.VisionModel;
import play.libs.F;
import utils.Context;

import java.util.List;

public class ContextVisionModel extends ContextModel implements VisionModel {

    @Inject
    private VisionModel visionModel;

    public ContextVisionModel(Context context) {
        this.context = context;
    }

    @Override
    public F.Tuple<List<String>, List<String>> annotate(Photo photo) {
        return call(() -> visionModel.annotate(photo));
    }
}
