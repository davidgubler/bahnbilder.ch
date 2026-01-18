package models;

import entities.Operator;
import entities.Wikidata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public interface OperatorsModel {
    Operator get(Integer id);

    Map<Integer, ? extends Operator> getByIdsAsMap(Collection<Integer> ids);

    Stream<? extends Operator> getByIds(Collection<Integer> ids);

    Operator getByName(String operator);

    void clear();

    Operator create(int id, String name, String abbr, List<String> wikiDataIds);

    Operator create(String name, String abbr, List<String> wikiDataIds);

    void update(Operator operator, String name, String abbr, List<String> wikiDataIds);

    Stream<? extends Operator> getAll();

    Stream<? extends Operator> getNotInIds(Collection<Integer> ids);

    Stream<? extends Operator> getNoWikidata();

    Stream<? extends Operator> getByAbbr(String abbr);

    void updateEras(Operator operator, Function<Collection<String>, List<Wikidata>> fetchWikidata);
}
