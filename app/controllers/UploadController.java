package controllers;

import biz.Photos;
import biz.ValidationException;
import com.google.inject.Inject;
import entities.Photo;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.InputUtils;
import utils.NotAllowedException;

import java.util.*;


public class UploadController extends Controller {
    @Inject
    private Photos photos;

    public Result upload(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        return ok(views.html.upload.upload.render(request, Collections.emptyList(), Collections.emptyMap(), InputUtils.NOERROR, user, lang));
    }

    public Result uploadPost(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        Http.MultipartFormData<Object> data = request.body().asMultipartFormData();

        List<String> fileNames = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();
        Map<String, Photo> successes = new HashMap<>();
        for (Http.MultipartFormData.FilePart<Object> filePart : data.getFiles()) {
            fileNames.add(filePart.getFilename());
            try {
                successes.put(filePart.getFilename(), photos.upload(context, filePart, user));
            } catch (ValidationException e) {
                errors.putAll(e.getErrors());
            }
        }
        return ok(views.html.upload.upload.render(request, fileNames, successes, errors, user, lang));
    }
}
