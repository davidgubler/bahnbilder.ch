package models.context;

import com.google.inject.Inject;
import models.ViewsModel;
import play.mvc.Http;
import utils.Context;

public class ContextViewsModel extends ContextModel implements ViewsModel {

    @Inject
    private ViewsModel viewsModel;

    public ContextViewsModel(Context context) {
        this.context = context;
    }

    @Override
    public void countView(int photoId, Http.Request request) {
        call(() -> { viewsModel.countView(photoId, request); return null; });
    }

    @Override
    public void collect() {
        call(() -> { viewsModel.collect(); return null; });
    }

    @Override
    public int getUncollected(int photoId) {
        return call(() -> viewsModel.getUncollected(photoId));
    }
}
