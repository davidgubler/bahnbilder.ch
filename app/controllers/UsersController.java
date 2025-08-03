package controllers;

import biz.Users;
import biz.ValidationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.InputUtils;
import utils.NotAllowedException;
import utils.NotFoundException;

import java.util.*;

public class UsersController extends Controller {

    @Inject
    private Users users;

    @Inject
    private Injector injector;

    public Result list(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null || user.isAdmin()) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<? extends User> users = context.getUsersModel().getAll().toList();
        return ok(views.html.users.list.render(request, users, user, lang));
    }

    public Result create(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null || user.isAdmin()) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        return ok(views.html.users.create.render(request, null, null, null, InputUtils.NOERROR, user, lang));
    }

    public Result createPost(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String email = InputUtils.trimToNull(data.get("email"));
        String name = InputUtils.trimToNull(data.get("name"));
        String password = InputUtils.trimToNull(data.get("password"));
        try {
            users.create(context, email, name, password, user);
            return redirect(controllers.routes.UsersController.list());
        } catch (ValidationException e) {
            return ok(views.html.users.create.render(request, email, name, password, e.getErrors(), user, lang));
        }
    }

    public Result edit(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null || user.isAdmin()) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        User editUser = context.getUsersModel().get(id);
        if (editUser == null) {
            throw new NotFoundException("User");
        }
        return ok(views.html.users.edit.render(request, editUser, editUser.getEmail(), editUser.getName(), null, InputUtils.NOERROR, user, lang));
    }

    public Result editPost(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        User editUser = context.getUsersModel().get(id);
        String email = InputUtils.trimToNull(data.get("email"));
        String name = InputUtils.trimToNull(data.get("name"));
        String password = InputUtils.trimToNull(data.get("password"));
        try {
            users.update(context, editUser, email, name, password, user);
            return redirect(controllers.routes.UsersController.list());
        } catch (ValidationException e) {
            return ok(views.html.users.edit.render(request, editUser, email, name, password, e.getErrors(), user, lang));
        }
    }

    public Result delete(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null || user.isAdmin()) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        User deleteUser = context.getUsersModel().get(id);
        if (deleteUser == null) {
            throw new NotFoundException("User");
        }
        return ok(views.html.users.delete.render(request, deleteUser, user, lang));
    }

    public Result deletePost(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        User deleteUser = context.getUsersModel().get(id);
        if (deleteUser == null) {
            throw new NotFoundException("User");
        }
        users.delete(context, deleteUser, user);
        return redirect(controllers.routes.UsersController.list());
    }
}
