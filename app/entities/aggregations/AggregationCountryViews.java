package entities.aggregations;

import entities.Country;

public interface AggregationCountryViews {
    Country getCountry();
    long getViews();
}
