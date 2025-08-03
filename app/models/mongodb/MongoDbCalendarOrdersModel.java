package models.mongodb;

import dev.morphia.aggregation.expressions.AccumulatorExpressions;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Group;
import dev.morphia.query.filters.Filters;
import entities.CalendarOrder;
import entities.mongodb.MongoDbCalendarOrder;
import models.CalendarOrdersModel;
import utils.Config;

import java.util.Collections;
import java.util.List;

public class MongoDbCalendarOrdersModel extends MongoDbModel<MongoDbCalendarOrder> implements CalendarOrdersModel {

    @Override
    public CalendarOrder orderPreview(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country) {
        return new MongoDbCalendarOrder(Config.Option.CALENDAR_YEAR.getInt(), nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country);
    }

    @Override
    public CalendarOrder order(String nrOfCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country) {
        MongoDbCalendarOrder calendarOrder = new MongoDbCalendarOrder(Config.Option.CALENDAR_YEAR.getInt(), nrOfCalendars, email, firstName, lastName, addressStreetAndNr, addressRemarks, zip, city, country);
        mongoDb.getDs().save(calendarOrder);
        return calendarOrder;
    }

    @Override
    public List<Integer> getYears() {
        List<Integer> years = mongoDb.getDs().aggregate(MongoDbCalendarOrder.class)
                .group(Group.group().field("_id", null).field("distinctIntegers", AccumulatorExpressions.addToSet(Expressions.field("orderYear"))))
                .execute(MongoDbPhotosModel.AggregationDistinct.class).next().distinctIntegers;
        Collections.sort(years);
        Collections.reverse(years);
        return years;
    }

    @Override
    public List<? extends CalendarOrder> getByYear(int year) {
        return query().filter(Filters.eq("orderYear", year)).stream().toList();
    }
}
