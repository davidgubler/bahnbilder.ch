package controllers;

import entities.IncompleteSearch;
import entities.Photo;
import entities.Search;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.NotAllowedException;

import java.util.List;


public class IncompleteController extends Controller {

    public Result list(Http.Request request, int page) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);

        Search search = new IncompleteSearch(user, page);
        long count = context.getPhotosModel().searchCount(search);
        int lastPage = search.getLastPage(count);
        search.adjustPage(lastPage);
        List<? extends Photo> photos = context.getPhotosModel().search(search);

        return ok(views.html.incomplete.list.render(request, search.getPage(), lastPage, photos, user, lang));
    }
}
