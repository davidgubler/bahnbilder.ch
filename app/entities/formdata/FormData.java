package entities.formdata;

import i18n.Lang;
import play.mvc.Http;
import utils.InputUtils;

public abstract class FormData<T> {
    public final String lang;
    public final String returnUrl;
    public final T entity;

    public FormData(Http.Request request, String returnUrl, T entity) {
        lang = Lang.get(request);
        this.returnUrl = InputUtils.sanitizeReturnUrl(returnUrl);
        this.entity = entity;
    }
}
