package entities.mongodb.aggregations;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import entities.ContextAwareEntity;
import entities.Country;
import entities.aggregations.AggregationCountryViews;
import utils.Context;

@Entity
public class MongoDbAggregationCountryViews implements AggregationCountryViews, ContextAwareEntity {
    @Id
    private int _id;

    private long views;

    @Transient
    private Context context;

    @Override
    public void inject(Context context) {
        this.context = context;
    }

    @Transient
    private Country country;

    public Country getCountry() {
        if (country == null) {
            country = context.getCountriesModel().get(_id);
        }
        return country;
    }

    public long getViews() {
        return views;
    }
}
