package models;

import entities.PhotoType;
import java.util.stream.Stream;

public interface PhotoTypesModel {
    void clear();
    PhotoType create(int id, String nameDe, String nameEn);
    PhotoType get(Integer id);
    Stream<? extends PhotoType> getAll();
}
