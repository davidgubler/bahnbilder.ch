package entities;

import i18n.Txt;
import utils.Config;

import java.time.Instant;

public interface CalendarOrder {
    Instant getTimestamp();

    String getNrOfCalendars();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getAddressStreetAndNr();

    String getAddressRemarks();

    String getZip();

    String getCity();

    String getCountry();

    default String getCalendarCost(String lang) {
        try {
            int nrOfCalendars = Integer.parseInt(getNrOfCalendars());
            if ("CH".equals(getCountry())) {
                return "CHF " + nrOfCalendars * Config.Option.CALENDAR_PRICE_CHF.getInt() + ".-";
            }
            if ("DE".equals(getCountry()) || "AT".equals(getCountry())) {
                return "€ " + nrOfCalendars * Config.Option.CALENDAR_PRICE_CHF.getInt() + ".-";
            }
            return Txt.get(lang, "asInvoiced");
        } catch (NumberFormatException e) {
            return Txt.get(lang, "asInvoiced");
        }
    }

    default String getShippingCost(String lang) {
        if ("CH".equals(getCountry())) {
            return "CHF " + Config.Option.CALENDAR_SHIPPING_CHF.getInt() + ".-";
        }
        if ("DE".equals(getCountry()) || "AT".equals(getCountry())) {
            return "€ " + Config.Option.CALENDAR_SHIPPING_EUR.getInt() + ".-";
        }
        return Txt.get(lang, "asInvoiced");
    }
}
