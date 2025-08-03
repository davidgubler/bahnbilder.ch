package entities.mongodb.aggregations;

import com.google.inject.Inject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import entities.Country;
import entities.aggregations.AggregationCountryViews;
import models.CountriesModel;

@Entity
public class MongoDbAggregationCountryViews implements AggregationCountryViews {
    @Id
    private int _id;

    private long views;

    @Transient
    @Inject
    private CountriesModel countriesModel;

    @Transient
    private Country country;

    public Country getCountry() {
        if (country == null) {
            country = countriesModel.get(_id);
        }
        return country;
    }

    public long getViews() {
        return views;
    }
}
