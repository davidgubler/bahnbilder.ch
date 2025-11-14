package models.context;

import com.google.inject.Inject;
import entities.RequestsDaily;
import models.RequestsDailyModel;
import utils.Context;

public class ContextRequestsDailyModel extends ContextModel implements RequestsDailyModel {
    @Inject
    private RequestsDailyModel requestsDailyModel;

    public ContextRequestsDailyModel(Context context) {
        this.context = context;
    }

    @Override
    public void track(String url, String referer) {
        call(() -> { requestsDailyModel.track(url, referer); return null; });
    }

    @Override
    public RequestsDaily getToday() {
        return call(() -> requestsDailyModel.getToday());
    }
}