package models.context;

import com.google.inject.Inject;
import entities.Country;
import entities.formdata.CountryFormData;
import models.CountriesModel;
import utils.Context;

import java.util.Collection;
import java.util.stream.Stream;

public class ContextCountriesModel extends ContextModel implements CountriesModel {

    @Inject
    private CountriesModel countriesModel;

    public ContextCountriesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { countriesModel.clear(); return null; });
    }

    @Override
    public Country create(int id, String code, String nameDe, String nameEn) {
        return call(() -> countriesModel.create(id, code, nameDe, nameEn));
    }

    @Override
    public Country create(CountryFormData data) {
        return call(() -> countriesModel.create(data));
    }

    @Override
    public Country get(Integer id) {
        return call(() -> countriesModel.get(id));
    }

    @Override
    public Country get(String id) {
        return call(() -> countriesModel.get(id));
    }

    @Override
    public Country getByCode(String code) {
        return call(() -> countriesModel.getByCode(code));
    }

    @Override
    public Country getByName(String name) {
        return call(() -> countriesModel.getByName(name));
    }

    @Override
    public Stream<? extends Country> getByIds(Collection<Integer> ids) {
        return call(() -> countriesModel.getByIds(ids));
    }

    @Override
    public Stream<? extends Country> getNotInIds(Collection<Integer> ids) {
        return call(() -> countriesModel.getNotInIds(ids));
    }

    @Override
    public Stream<? extends Country> getAll() {
        return call(() -> countriesModel.getAll());
    }

    @Override
    public void update(Country country, String nameDe, String nameEn, String shortName) {
        call(() -> { countriesModel.update(country, nameDe, nameEn, shortName); return null;});
    }

    @Override
    public void delete(Country country) {
        call(() -> { countriesModel.delete(country); return null; });
    }
}
