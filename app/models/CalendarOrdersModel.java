package models;

import entities.CalendarOrder;

import java.util.List;

public interface CalendarOrdersModel {
    CalendarOrder orderPreview(int nrOfRailCalendars, int nrOfAnimalCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country);

    CalendarOrder order(int nrOfRailCalendars, int nrOfAnimalCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country);

    List<Integer> getYears();

    List<? extends CalendarOrder> getByYear(int year);
}
