package models;

import entities.CalendarOrder;

import java.util.List;

public interface CalendarOrdersModel {
    CalendarOrder orderPreview(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country);

    CalendarOrder order(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country);

    List<Integer> getYears();

    List<? extends CalendarOrder> getByYear(int year);
}
