package entities.formdata;

import entities.Operator;
import play.mvc.Http;
import utils.InputUtils;
import utils.StringUtils;

import java.util.Map;

public class OperatorFormData extends FormData<Operator> {
    public final String name;
    public final String abbr;
    public final String wikiData;

    public OperatorFormData(Http.Request request, String returnUrl, Operator operator) {
        super(request, returnUrl, operator);
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            name = InputUtils.trimToNull(data.get("name"));
            abbr = InputUtils.trimToNull(data.get("abbr"));
            wikiData = InputUtils.trimToNull(data.get("wikiData"));
        } else {
            name = operator == null ? null : operator.getName();
            abbr = operator == null ? null : operator.getAbbr();
            wikiData = operator == null ? null : StringUtils.join(operator.getWikiDataIds(), ",");
        }
    }
}
