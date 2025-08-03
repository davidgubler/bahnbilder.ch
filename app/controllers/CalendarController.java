package controllers;

import biz.Calendars;
import biz.ValidationException;
import com.google.inject.Inject;
import entities.CalendarOrder;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Config;
import utils.Context;
import utils.InputUtils;
import utils.NotAllowedException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CalendarController extends Controller {
    @Inject
    private Calendars calendars;

    public Result view(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        return ok(views.html.calendar.view.render(request,
                Config.Option.CALENDAR_PRICE_CHF.getInt(),
                Config.Option.CALENDAR_PRICE_EUR.getInt(),
                Config.Option.CALENDAR_SHIPPING_CHF.getInt(),
                Config.Option.CALENDAR_SHIPPING_EUR.getInt(),
                Config.Option.CALENDAR_YEAR.getInt(),
                Config.Option.CALENDAR_ORDER_AVAILABLE.getBool(),
                user, lang));
    }

    public Result order(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);

        return ok(views.html.calendar.order.render(request,
                Config.Option.CALENDAR_PRICE_CHF.getInt(),
                Config.Option.CALENDAR_PRICE_EUR.getInt(),
                Config.Option.CALENDAR_SHIPPING_CHF.getInt(),
                Config.Option.CALENDAR_SHIPPING_EUR.getInt(),
                "1", null, null, null, null, null, null, null, null, null, null, null, false, Collections.emptyMap(), user, lang));
    }

    public Result orderPost(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        String lang = Lang.get(request);
        Map<String, String[]> data = request.body().asFormUrlEncoded();

        String submit = InputUtils.trimToNull(data.get("submit"));
        String nrOfCalendars = InputUtils.trimToNull(data.get("nrOfCalendars"));
        String nrOfCalendarsOther = InputUtils.trimToNull(data.get("nrOfCalendarsOther"));
        String email = InputUtils.trimToNull(data.get("email"));
        String emailConfirm = InputUtils.trimToNull(data.get("emailConfirm"));
        String firstName = InputUtils.trimToNull(data.get("firstName"));
        String lastName = InputUtils.trimToNull(data.get("lastName"));
        String addressStreetAndNr = InputUtils.trimToNull(data.get("addressStreetAndNr"));
        String addressRemarks = InputUtils.trimToNull(data.get("addressRemarks"));
        String zip = InputUtils.trimToNull(data.get("zip"));
        String city = InputUtils.trimToNull(data.get("city"));
        String country = InputUtils.trimToNull(data.get("country"));
        String countryOther = InputUtils.trimToNull(data.get("countryOther"));
        boolean acceptTC = Boolean.TRUE.toString().equalsIgnoreCase(InputUtils.trimToNull(data.get("acceptTC")));

        if ("form".equals(submit)) {
            return ok(views.html.calendar.order.render(request,
                    Config.Option.CALENDAR_PRICE_CHF.getInt(),
                    Config.Option.CALENDAR_PRICE_EUR.getInt(),
                    Config.Option.CALENDAR_SHIPPING_CHF.getInt(),
                    Config.Option.CALENDAR_SHIPPING_EUR.getInt(),
                    nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, Collections.emptyMap(), user, lang));
        }
        if ("order".equals(submit)) {
            try {
                calendars.order(context, nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, lang);
                return ok(views.html.calendar.orderConfirm.render(request, email, user, lang));
            } catch (ValidationException e) {
                return ok(views.html.calendar.order.render(request,
                        Config.Option.CALENDAR_PRICE_CHF.getInt(),
                        Config.Option.CALENDAR_PRICE_EUR.getInt(),
                        Config.Option.CALENDAR_SHIPPING_CHF.getInt(),
                        Config.Option.CALENDAR_SHIPPING_EUR.getInt(),
                        nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, e.getErrors(), user, lang));
            }
        }

        try {
            CalendarOrder orderPreview = calendars.orderPreview(context, nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, lang);
            return ok(views.html.calendar.orderPreview.render(request, orderPreview, nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, user, lang));
        } catch (ValidationException e) {
            return ok(views.html.calendar.order.render(request,
                    Config.Option.CALENDAR_PRICE_CHF.getInt(),
                    Config.Option.CALENDAR_PRICE_EUR.getInt(),
                    Config.Option.CALENDAR_SHIPPING_CHF.getInt(),
                    Config.Option.CALENDAR_SHIPPING_EUR.getInt(),
                    nrOfCalendars, nrOfCalendarsOther, email, emailConfirm, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country, countryOther, acceptTC, e.getErrors(), user, lang));
        }
    }

    public Result list(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<Integer> years = context.getCalendarOrdersModel().getYears();
        List<? extends CalendarOrder> orders = context.getCalendarOrdersModel().getByYear(Config.Option.CALENDAR_YEAR.getInt());
        return ok(views.html.calendar.list.render(request, Config.Option.CALENDAR_YEAR.getInt(), years, orders, user, lang));
    }

    public Result listPost(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);
        List<Integer> years = context.getCalendarOrdersModel().getYears();
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        Integer year = InputUtils.toInt(data.get("year"));
        if (year == null) {
            year = Config.Option.CALENDAR_YEAR.getInt();
        }
        List<? extends CalendarOrder> orders = context.getCalendarOrdersModel().getByYear(year);
        return ok(views.html.calendar.list.render(request, year, years, orders, user, lang));
    }
}
