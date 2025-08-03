package entities.formdata;

import entities.Country;
import play.mvc.Http;
import utils.InputUtils;

import java.util.Map;

public class CountryFormData extends FormData<Country> {
    public final String nameDe;
    public final String nameEn;
    public final String code;

    public CountryFormData(Http.Request request, String returnUrl, Country country) {
        super(request, returnUrl, country);
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            nameDe = InputUtils.trimToNull(data.get("nameDe"));
            nameEn = InputUtils.trimToNull(data.get("nameEn"));
            code = InputUtils.trimToNull(data.get("code"));
        } else {
            nameDe = country == null ? null : country.getName("de");
            nameEn = country == null ? null : country.getName("en");
            code = country == null ? null : country.getCode();
        }
    }
}
