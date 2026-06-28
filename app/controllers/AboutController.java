package controllers;

import com.google.inject.Inject;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.MongoDb;
import utils.Context;
import utils.NotFoundException;

public class AboutController extends Controller {
    @Inject
    private MongoDb mongoDb;

    public Result view(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.about.view.render(request, user, lang));
    }

    public Result user(Http.Request request, Integer userId) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        User aboutUser = context.getUsersModel().get(userId);
        if (aboutUser == null) {
            throw new NotFoundException("user");
        }

        int userSince = 2012;

        return ok(views.html.about.user.render(request, aboutUser, userSince, user, lang));
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
