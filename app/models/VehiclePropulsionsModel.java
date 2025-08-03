package models;

import entities.VehiclePropulsion;

import java.util.stream.Stream;

public interface VehiclePropulsionsModel {
    void clear();

    VehiclePropulsion create(int id, String nameDe, String nameEn);

    VehiclePropulsion get(Integer id);

    Stream<? extends VehiclePropulsion> getAll();
}
