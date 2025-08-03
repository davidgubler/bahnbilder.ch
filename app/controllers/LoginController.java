package controllers;

import biz.Login;
import biz.Users;
import biz.ValidationException;
import com.google.inject.Inject;
import entities.Session;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.Generator;
import utils.InputUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LoginController extends Controller {

    @Inject
    private Users users;

    @Inject
    private Login login;

    public Result login(Http.Request request) {
        Context context = Context.get(request);
        users.ensureAdmin(context);
        String lang = Lang.get(request);
        return ok(views.html.login.login.render(request, null, null, InputUtils.NOERROR, lang));
    }

    public Result loginPost(Http.Request request) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String email = InputUtils.trimToNull(data.get("email"));
        String password = InputUtils.trimToNull(data.get("password"));
        try {
            User user = login.login(context, email, password);
            List<? extends Session> sessions = user.getSessions();
            Session session = sessions.get(sessions.size() - 1);
            Http.Cookie sessionCookie = Http.Cookie.builder("sessionId", session.getSessionId()).withMaxAge(Duration.ofDays(365)).build();
            Http.Cookie csrfTokenCookie = Http.Cookie.builder("csrfToken", Generator.generateSessionId()).withMaxAge(Duration.ofDays(365)).withHttpOnly(false).build();
            return redirect(routes.HomeController.home()).withCookies(sessionCookie, csrfTokenCookie);
        } catch (ValidationException e) {
            return ok(views.html.login.login.render(request, email, password, e.getErrors(), lang));
        }
    }

    public Result logout(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user != null) {
            login.logout(context, user);
        }
        Http.Cookie sessionCookie = Http.Cookie.builder("sessionId", "").withMaxAge(Duration.ZERO).build();
        Http.Cookie csrfTokenCookie = Http.Cookie.builder("csrfToken", "").withMaxAge(Duration.ZERO).build();
        return redirect(routes.HomeController.home()).withCookies(sessionCookie, csrfTokenCookie);
    }

    public Result lostPassword(Http.Request request) {
        String lang = Lang.get(request);
        return ok(views.html.login.lostPassword.render(request, null, InputUtils.NOERROR, lang));
    }

    public Result lostPasswordPost(Http.Request request) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String email = InputUtils.trimToNull(data.get("email"));
        try {
            login.lostPassword(context, email, lang);
            return ok(views.html.login.lostPasswordEmailSent.render(request, lang));
        } catch (ValidationException e) {
            return ok(views.html.login.lostPassword.render(request, email, e.getErrors(), lang));
        }
    }

    public Result linkLogin(Http.Request request, Long ts, String email, String sig) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        try {
            User user = login.linkLogin(context, ts, email, sig);
            List<? extends Session> sessions = user.getSessions();
            Session session = sessions.get(sessions.size() - 1);
            Http.Cookie sessionCookie = Http.Cookie.builder("sessionId", session.getSessionId()).withMaxAge(Duration.ofDays(365)).build();
            Http.Cookie csrfTokenCookie = Http.Cookie.builder("csrfToken", Generator.generateSessionId()).withMaxAge(Duration.ofDays(365)).withHttpOnly(false).build();
            return redirect(routes.HomeController.home()).withCookies(sessionCookie, csrfTokenCookie);
        } catch (ValidationException e) {
            return ok(views.html.login.lostPasswordLinkExpired.render(request, lang));
        }
    }

    public Result newPw(Http.Request request, Long ts, String email, String sig) {
        Context context = Context.get(request);
        String lang = Lang.get(request);
        try {
            String password = login.newPw(context, ts, email, sig);
            return ok(views.html.login.lostPasswordNewPw.render(request, password, lang));
        } catch (ValidationException e) {
            return ok(views.html.login.lostPasswordLinkExpired.render(request, lang));
        }
    }
}
