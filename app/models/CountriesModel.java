package models;

import entities.Country;
import entities.formdata.CountryFormData;

import java.util.Collection;
import java.util.stream.Stream;

public interface CountriesModel {
    void clear();

    Country create(int id, String code, String nameDe, String nameEn);

    Country create(CountryFormData data);

    Country get(Integer id);

    Country get(String id);

    Country getByCode(String code);

    Country getByName(String name);

    Stream<? extends Country> getByIds(Collection<Integer> ids);

    Stream<? extends Country> getNotInIds(Collection<Integer> ids);

    Stream<? extends Country> getAll();

    void update(Country country, String nameDe, String nameEn, String shortName);

    void delete(Country country);
}
