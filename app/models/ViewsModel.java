package models;

import play.mvc.Http;

public interface ViewsModel {
    void countView(int photoId, Http.Request request);

    void collect();

    int getUncollected(int photoId);
}
