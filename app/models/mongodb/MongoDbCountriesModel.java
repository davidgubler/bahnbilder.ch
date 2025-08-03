package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.Country;
import entities.formdata.CountryFormData;
import entities.mongodb.MongoDbCountry;
import models.CountriesModel;

import java.util.Collection;
import java.util.stream.Stream;

public class MongoDbCountriesModel extends MongoDbModel<MongoDbCountry> implements CountriesModel {
    @Override
    public Country create(int id, String code, String nameDe, String nameEn) {
        Country country = new MongoDbCountry(id, code, nameDe, nameEn);
        mongoDb.getDs().save(country);
        return country;
    }

    @Override
    public Country create(CountryFormData data) {
        Country country = null;
        for (int i = 0; i < 10; i++) {
            try {
                country = new MongoDbCountry(getNextNumId(), data.code, data.nameDe, data.nameEn);
                mongoDb.getDs().save(country);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return country;
    }

    @Override
    public Country getByCode(String code) {
        return query().filter(Filters.eq("code", code)).first();
    }

    @Override
    public Country getByName(String name) {
        return query().filter(Filters.or(Filters.eq("names.de", name), Filters.eq("names.en", name))).first();
    }

    @Override
    public Stream<MongoDbCountry> getNotInIds(Collection<Integer> ids) {
        return query().filter(Filters.nin("numId", ids)).stream();
    }

    @Override
    public void update(Country country, String nameDe, String nameEn, String code) {
        MongoDbCountry mongoDbCountry = (MongoDbCountry)country;
        mongoDbCountry.setName("de", nameDe);
        mongoDbCountry.setName("en", nameEn);
        mongoDbCountry.setCode(code);
        query(country).update(new UpdateOptions(), UpdateOperators.set("names", mongoDbCountry.getNames()), UpdateOperators.set("code", mongoDbCountry.getCode()));
    }

    @Override
    public void delete(Country country) {
        super.delete(country);
    }
}
