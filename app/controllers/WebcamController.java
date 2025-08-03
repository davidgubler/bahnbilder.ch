package controllers;

import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;


public class WebcamController extends Controller {

    public Result view(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.webcam.view.render(request, user, lang));
    }
}
