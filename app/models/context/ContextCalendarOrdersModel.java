package models.context;

import com.google.inject.Inject;
import entities.CalendarOrder;
import models.CalendarOrdersModel;
import utils.Context;

import java.util.List;

public class ContextCalendarOrdersModel extends ContextModel implements CalendarOrdersModel {
    @Inject
    private CalendarOrdersModel calendarOrdersModel;

    public ContextCalendarOrdersModel(Context context) {
        this.context = context;
    }

    @Override
    public CalendarOrder orderPreview(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country) {
        return call(() -> calendarOrdersModel.order(nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country));
    }

    @Override
    public CalendarOrder order(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country) {
        return call(() -> calendarOrdersModel.order(nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country));
    }

    @Override
    public List<Integer> getYears() {
        return call(() -> calendarOrdersModel.getYears());
    }

    @Override
    public List<? extends CalendarOrder> getByYear(int year) {
        return call(() -> calendarOrdersModel.getByYear(year));
    }
}
