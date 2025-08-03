package controllers;

import com.google.inject.Inject;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.MongoDb;
import utils.Context;

public class AboutController extends Controller {
    @Inject
    private MongoDb mongoDb;

    public Result view(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.about.view.render(request, user, lang));
    }

    public Result privacy(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.about.privacy.render(request, user, lang));
    }

    public Result status(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.about.status.render(request, mongoDb.isWritable(), mongoDb.getReplSetStatus(), user, lang));
    }
}
