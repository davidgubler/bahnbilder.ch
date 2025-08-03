package controllers;

import biz.Travelogues;
import biz.ValidationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.Travelogue;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TraveloguesController extends Controller {
    @Inject
    private Travelogues travelogues;

    @Inject
    private Injector injector;

    public Result list(Http.Request request, String yearMonth) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        List<? extends Travelogue> travelogues = new ArrayList<>(context.getTraveloguesModel().getAll().sorted().toList());
        Collections.reverse(travelogues);
        LocalDate firstDate = travelogues.isEmpty() ? LocalDate.now() : travelogues.get(0).getDate();

        if (yearMonth == null) {
            yearMonth = "%4d-%02d".formatted(firstDate.getYear(), firstDate.getMonthValue());
        }

        Map<String, List<Travelogue>> groupedByMonth = travelogues.stream().filter(t -> t.getDate() != null).collect(Collectors.groupingBy(t -> "%4d-%02d".formatted(t.getDate().getYear(), t.getDate().getMonthValue())));

        return ok(views.html.travelogues.list.render(request, groupedByMonth, yearMonth, user, lang));
    }

    public Result view(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (travelogue == null) {
            throw new NotFoundException("Travelogue");
        }


        return ok(views.html.travelogues.view.render(request, travelogue, user, lang));
    }

    public Result create(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        return ok(views.html.travelogues.edit.render(request, null, null, null, null, null, null, Collections.emptyMap(), user, lang));
    }

    public Result createPost(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String title = InputUtils.trimToNull(data.get("title"));
        String titlePhoto = InputUtils.trimToNull(data.get("titlePhoto"));
        String date = InputUtils.trimToNull(data.get("date"));
        String summary = InputUtils.trimToNull(data.get("summary"));
        String text = InputUtils.trimToNull(data.get("text"));
        try {
            Travelogue travelogue = travelogues.create(context, title, titlePhoto, date, summary, text, user);
            return redirect(routes.TraveloguesController.view(travelogue.getId()));
        } catch (ValidationException e) {
            return ok(views.html.travelogues.edit.render(request, null, title, titlePhoto, date, summary, text, e.getErrors(), user, lang));
        }
    }

    public Result edit(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotFoundException("Travelogue");
        }
        return ok(views.html.travelogues.edit.render(request, travelogue, travelogue.getTitle(), travelogue.getTitlePhotoId() == null ? null : "" + travelogue.getTitlePhotoId(), StringUtils.formatDate(lang, travelogue.getDate()), travelogue.getSummary(), travelogue.getText(), Collections.emptyMap(), user, lang));
    }

    public Result editPost(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotFoundException("Travelogue");
        }
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String title = InputUtils.trimToNull(data.get("title"));
        String titlePhoto = InputUtils.trimToNull(data.get("titlePhoto"));
        String date = InputUtils.trimToNull(data.get("date"));
        String summary = InputUtils.trimToNull(data.get("summary"));
        String text = InputUtils.trimToNull(data.get("text"));
        try {
            travelogues.update(context, travelogue, title, titlePhoto, date, summary, text, user);
            return redirect(routes.TraveloguesController.view(travelogue.getId()));
        } catch (ValidationException e) {
            return ok(views.html.travelogues.edit.render(request, travelogue, title, titlePhoto, date, summary, text, e.getErrors(), user, lang));
        }
    }

    public Result delete(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotFoundException("Travelogue");
        }
        return ok(views.html.travelogues.delete.render(request, travelogue, user, lang));
    }

    public Result deletePost(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (travelogue == null) {
            throw new NotFoundException("Travelogue");
        }
        travelogues.delete(context, travelogue, user);
        return redirect(routes.TraveloguesController.list(null));
    }

    public Result bb(Http.Request request, Integer id) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Travelogue travelogue = context.getTraveloguesModel().get(id);
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotAllowedException();
        }

        return ok(views.html.travelogues.bb.render(request, travelogue, user, lang));
    }
}
