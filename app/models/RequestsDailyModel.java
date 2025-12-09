package models;

import entities.RequestsDaily;

import java.time.LocalDate;
import java.util.List;

public interface RequestsDailyModel {
    void track(String url, String referer);

    RequestsDaily getToday();

    List<? extends RequestsDaily> getRange(LocalDate from, LocalDate before);

    LocalDate getFirstDate();
}
