package models;

import entities.RequestsDaily;

public interface RequestsDailyModel {
    void track(String url, String referer);

    RequestsDaily getToday();
}
