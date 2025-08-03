package biz;

import com.google.inject.Inject;
import entities.CalendarOrder;
import services.Mail;
import utils.BahnbilderLogger;
import utils.Context;
import utils.ErrorMessages;
import utils.InputUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Calendars {

    @Inject
    private Mail mail;

    public CalendarOrder orderPreview(Context context, String nrOfCalendars, String nrOfCalendarsOther, String email, String emailConfirm, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country, String countryOther, boolean acceptTC, String lang) throws ValidationException {
        // ACCESS
        // anybody can order calendars

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(nrOfCalendars, "nrOfCalendars", errors);
        if ("other".equals(nrOfCalendars)) {
            InputUtils.validateString(nrOfCalendarsOther, "nrOfCalendarsOther", errors);
            nrOfCalendars = nrOfCalendarsOther;
        }
        InputUtils.validateEmail(email, "email", true, errors);
        InputUtils.validateString(emailConfirm, "emailConfirm", errors);
        if (!Objects.equals(email, emailConfirm)) {
            errors.put("emailConfirm", ErrorMessages.INVALID_EMAIL_DOES_NOT_MATCH);
        }
        InputUtils.validateString(firstName, "firstName", errors);
        InputUtils.validateString(lastName, "lastName", errors);
        InputUtils.validateString(addressStreetAndNr, "addressStreetAndNr", errors);
        // not validating addressRemarks, it's optional
        InputUtils.validateString(zip, "zip", errors);
        InputUtils.validateString(city, "city", errors);
        InputUtils.validateString(city, "city", errors);
        InputUtils.validateString(country, "country", errors);
        if ("other".equals(country)) {
            InputUtils.validateString(countryOther, "countryOther", errors);
            country = countryOther;
        }
        if (!acceptTC) {
            errors.put("acceptTC", ErrorMessages.ACCEPT_TC);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        CalendarOrder orderPreview = context.getCalendarOrdersModel().orderPreview(nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country);

        // LOG
        // nothing yet

        return orderPreview;
    }

    public void order(Context context, String nrOfCalendars, String nrOfCalendarsOther, String email, String emailConfirm, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country, String countryOther, boolean acceptTC, String lang) throws ValidationException {
        // ACCESS
        // anybody can order calendars

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(nrOfCalendars, "nrOfCalendars", errors);
        if ("other".equals(nrOfCalendars)) {
            InputUtils.validateString(nrOfCalendarsOther, "nrOfCalendarsOther", errors);
            nrOfCalendars = nrOfCalendarsOther;
        }
        InputUtils.validateEmail(email, "email", true, errors);
        InputUtils.validateString(emailConfirm, "emailConfirm", errors);
        if (!Objects.equals(email, emailConfirm)) {
            errors.put("emailConfirm", ErrorMessages.INVALID_EMAIL_DOES_NOT_MATCH);
        }
        InputUtils.validateString(firstName, "firstName", errors);
        InputUtils.validateString(lastName, "lastName", errors);
        InputUtils.validateString(addressStreetAndNr, "addressStreetAndNr", errors);
        // not validating addressRemarks, it's optional
        InputUtils.validateString(zip, "zip", errors);
        InputUtils.validateString(city, "city", errors);
        InputUtils.validateString(city, "city", errors);
        InputUtils.validateString(country, "country", errors);
        if ("other".equals(country)) {
            InputUtils.validateString(countryOther, "countryOther", errors);
            country = countryOther;
        }
        if (!acceptTC) {
            errors.put("acceptTC", ErrorMessages.ACCEPT_TC);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        CalendarOrder order = context.getCalendarOrdersModel().order(nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country);
        mail.caledarConfirmation(order, lang);

        // LOG
        BahnbilderLogger.info(context.getRequest(), "Ordered calendars: " + nrOfCalendars);
    }
}
