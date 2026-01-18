package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.Operator;
import entities.Wikidata;
import entities.mongodb.MongoDbOperator;
import entities.mongodb.MongoDbOperatorEra;
import models.OperatorsModel;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class MongoDbOperatorsModel extends MongoDbModel<MongoDbOperator> implements OperatorsModel {
    @Override
    public Operator create(int id, String name, String abbr, List<String> wikiDataIds) {
        Operator operator = new MongoDbOperator(id, name, abbr, wikiDataIds);
        mongoDb.getDs().save(operator);
        return operator;
    }

    @Override
    public Operator create(String name, String abbr, List<String> wikiDataIds) {
        Operator operator = null;
        for (int i = 0; i < 10; i++) {
            try {
                operator = new MongoDbOperator(getNextNumId(), name, abbr, wikiDataIds);
                mongoDb.getDs().save(operator);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return operator;
    }

    @Override
    public void update(Operator operator, String name, String abbr, List<String> wikiDataIds) {
        MongoDbOperator mongoDbOperator = (MongoDbOperator)operator;
        mongoDbOperator.setName(name);
        mongoDbOperator.setAbbr(abbr);
        mongoDbOperator.setWikiDataIds(wikiDataIds);
        mongoDbOperator.setErasLastRefresh(null);
        query(operator).update(new UpdateOptions(), UpdateOperators.set("name", mongoDbOperator.getName()), UpdateOperators.set("abbr", mongoDbOperator.getAbbr()), UpdateOperators.set("wikiDataIds", mongoDbOperator.getWikiDataIds()), UpdateOperators.set("erasLastRefresh", null));
    }

    @Override
    public Operator getByName(String name) {
        return query().filter(Filters.eq("name", name)).first();
    }

    @Override
    public Stream<MongoDbOperator> getNotInIds(Collection<Integer> ids) {
        return query().filter(Filters.nin("numId", ids)).stream();
    }

    @Override
    public Stream<? extends Operator> getNoWikidata() {
        return query().filter(Filters.eq("wikiDataIds", null)).stream();
    }

    @Override
    public Stream<? extends Operator> getByAbbr(String abbr) {
        return query().filter(Filters.eq("abbr", abbr)).stream();
    }

    @Override
    public void updateEras(Operator operator, Function<Collection<String>, List<Wikidata>> fetchWikidata) {
        Instant now = Instant.now();
        MongoDbOperator mongoDbOperator = (MongoDbOperator)operator;
        // avoid parallel refreshes
        query(operator).update(new UpdateOptions(), UpdateOperators.set("erasLastRefresh", now));
        mongoDbOperator.setErasLastRefresh(now);

        // do actual refresh in background, may be slow (fetching Wikidata)
        new Thread(() -> {
            List<Wikidata> wikidata = new ArrayList<>(fetchWikidata.apply(operator.getWikiDataIds()));
            Collections.sort(wikidata);
            Collections.reverse(wikidata);
            List<MongoDbOperatorEra> eras = new ArrayList<>();
            LocalDate nextInception = null;
            for(Wikidata d : wikidata) {
                LocalDate dissolved = d.getDissolved();
                if (dissolved == null && nextInception != null) {
                    dissolved = nextInception;
                }
                eras.add(new MongoDbOperatorEra(d.getName("de"), d.getName("en"), d.getLogoUrl(), d.getLogoSrc(), d.getInceptedYear(), dissolved == null ? null : dissolved.plusDays(-1).getYear(), d.getRef()));
                nextInception = d.getIncepted();
            }
            query(operator).update(new UpdateOptions(), UpdateOperators.set("erasLastRefresh", now), UpdateOperators.set("eras", eras));
        }).run();
    }
}
