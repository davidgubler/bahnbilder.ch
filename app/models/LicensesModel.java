package models;

import entities.License;

import java.util.List;

public interface LicensesModel {
    License get(Integer id);

    List<? extends License> getAll();
}
