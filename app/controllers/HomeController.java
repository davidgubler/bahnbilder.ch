package controllers;

import entities.Photo;
import entities.Travelogue;
import entities.User;
import entities.aggregations.AggregationCountryViews;
import i18n.Lang;
import play.mvc.*;
import play.mvc.Http.Cookie;
import utils.Config;
import utils.Context;

import java.util.List;

public class HomeController extends Controller {

    public Result home(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        List<Photo> featured = context.getPhotosModel().getFeatured(context.getVehicleClassesModel(), context.getVehicleTypesModel());
        List<? extends Travelogue> travelogues = context.getTraveloguesModel().getFeatured();
        List<? extends AggregationCountryViews> countryViews = context.getPhotosModel().getTopCountryIdsByViews();

        return ok(views.html.home.render(request, featured, travelogues, countryViews, user, lang));
    }

    public Result lang(Http.Request request, String lang, String returnUrl) {
        String dstHost = Config.Option.HOST_DE.get();
        if ("en".equals(lang)) {
            dstHost = Config.Option.HOST_EN.get();
        }
        String url;
        if (dstHost == null) {
            url = "http://localhost:9000" + returnUrl;
        } else {
            url = "https://" + dstHost + returnUrl;
        }
        return redirect(url).withCookies(Cookie.builder("lang", lang).build());
    }
}
