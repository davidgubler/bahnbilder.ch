package models;

import entities.VehicleType;

import java.util.Collection;
import java.util.stream.Stream;

public interface VehicleTypesModel {
    void clear();

    VehicleType create(int id, String nameDe, String nameEn, String pluralDe, String pluralEn, int order);

    VehicleType get(Integer id);

    Stream<? extends VehicleType> getAll();

    Stream<? extends VehicleType> getByIds(Collection<Integer> ids);
}
