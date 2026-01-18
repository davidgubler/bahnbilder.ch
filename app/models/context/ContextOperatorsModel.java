package models.context;

import com.google.inject.Inject;
import entities.Operator;
import entities.Wikidata;
import models.OperatorsModel;
import utils.Context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class ContextOperatorsModel extends ContextModel implements OperatorsModel {

    @Inject
    private OperatorsModel operatorsModel;

    public ContextOperatorsModel(Context context) {
        this.context = context;
    }

    @Override
    public Operator get(Integer id) {
        return call(() -> operatorsModel.get(id));
    }

    @Override
    public Map<Integer, ? extends Operator> getByIdsAsMap(Collection<Integer> ids) {
        return call(() -> operatorsModel.getByIdsAsMap(ids));
    }

    @Override
    public Stream<? extends Operator> getByIds(Collection<Integer> ids) {
        return call(() -> operatorsModel.getByIds(ids));
    }

    @Override
    public Operator getByName(String operator) {
        return call(() -> operatorsModel.getByName(operator));
    }

    @Override
    public void clear() {
        call(() -> { operatorsModel.clear(); return null; });
    }

    @Override
    public Operator create(int id, String name, String abbr, List<String> wikiDataIds) {
        return call(() -> operatorsModel.create(id, name, abbr, wikiDataIds));
    }

    @Override
    public Operator create(String name, String abbr, List<String> wikiDataIds) {
        return call(() -> operatorsModel.create(name, abbr, wikiDataIds));
    }

    @Override
    public void update(Operator operator, String name, String abbr, List<String> wikiDataIds) {
        call(() -> { operatorsModel.update(operator, name, abbr, wikiDataIds); return null; });
    }

    @Override
    public Stream<? extends Operator> getAll() {
        return call(() -> operatorsModel.getAll());
    }

    @Override
    public Stream<? extends Operator> getNotInIds(Collection<Integer> ids) {
        return call(() -> operatorsModel.getNotInIds(ids));
    }

    @Override
    public Stream<? extends Operator> getNoWikidata() {
        return call(() -> operatorsModel.getNoWikidata());
    }

    @Override
    public Stream<? extends Operator> getByAbbr(String abbr) {
        return call(() -> operatorsModel.getByAbbr(abbr));
    }

    @Override
    public void updateEras(Operator operator, Function<Collection<String>, List<Wikidata>> fetchWikidata) {
        call(() -> { operatorsModel.updateEras(operator, fetchWikidata); return null; });
    }
}
