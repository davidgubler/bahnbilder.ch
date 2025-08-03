package entities.formdata;

import entities.Keyword;
import play.mvc.Http;
import utils.InputUtils;
import utils.StringUtils;

import java.util.Map;

public class KeywordFormData extends FormData<Keyword> {
    public final String nameDe;
    public final String nameEn;
    public final String labels;

    public KeywordFormData(Http.Request request, String returnUrl, Keyword keyword) {
        super(request, returnUrl, keyword);
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            nameDe = InputUtils.trimToNull(data.get("nameDe"));
            nameEn = InputUtils.trimToNull(data.get("nameEn"));
            labels = InputUtils.trimToNull(data.get("labels"));
        } else {
            nameDe = keyword == null ? null : keyword.getName("de");
            nameEn = keyword == null ? null : keyword.getName("en");
            labels = keyword == null ? null : StringUtils.join(keyword.getLabels(), ",");
        }
    }
}
