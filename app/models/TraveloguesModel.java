package models;

import entities.Travelogue;
import entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface TraveloguesModel {
    void clear();

    Travelogue create(int id, String title, Integer titlePhotoId, LocalDate date, String summary, String text, int userId);

    Travelogue create(String title, Integer titlePhotoId, LocalDate date, String summary, String text, User user);

    void update(Travelogue travelogue, String title, Integer titlePhotoId, LocalDate date, String summary, String text);

    void delete(Travelogue travelogue);

    Travelogue get(Integer id);

    List<? extends Travelogue> getFeatured();

    Stream<? extends Travelogue> getAll();
}
