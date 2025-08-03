package controllers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.*;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;

import java.util.List;

public class NewestController extends Controller {

    @Inject
    private Injector injector;

    public Result list(Http.Request request, int page) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        Search search = new Search(Search.SortBy.uploadDate, page);
        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        return ok(views.html.newest.list.render(request, search.getPage(), lastPage, photos, user, lang));
    }
}
