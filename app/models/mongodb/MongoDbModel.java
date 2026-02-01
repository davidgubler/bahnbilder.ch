package models.mongodb;

import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import entities.mongodb.MongoDbEntity;
import services.MongoDb;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MongoDbModel<T extends MongoDbEntity> {
    @Inject
    protected MongoDb mongoDb;

    private final Class<T> clazz;

    public MongoDbModel() {
        // Some shenanigans required to get a class reference
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType parameterized = (ParameterizedType) superclass;
        clazz = (Class<T>) parameterized.getActualTypeArguments()[0];
    }

    protected Datastore getDs() {
        return mongoDb.getDs();
    }

    public void clear() {
        getDs().getDatabase().getCollection(clazz.getAnnotation(Entity.class).value()).drop();
    }

    protected Query<T> query() {
        return getDs().find(clazz);
    }

    protected Query<T> query(Object entity) {
        return query().filter(Filters.eq("_id", ((MongoDbEntity)entity).getObjectId()));
    }

    public T get(Integer id) {
        if (id == null) {
            return null;
        }
        return query().filter(Filters.eq("numId", id)).first();
    }

    public Stream<T> getByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Stream.empty();
        }
        return query().filter(Filters.in("numId", ids)).stream();
    }

    public Map<Integer, T> getByIdsAsMap(Collection<Integer> ids) {
        return getByIds(ids).collect(Collectors.toMap(T::getId, Function.identity()));
    }

    public Stream<T> getAll() {
        return query().stream();
    }

    public void delete(Object entity) {
        query(entity).delete();
    }

    public int getNextNumId() {
        final String FIELD = "numId";
        FindOptions findOptions = new FindOptions().sort(dev.morphia.query.Sort.descending(FIELD)).limit(1);
        findOptions.projection().include(FIELD);
        T entitiy = query().filter(Filters.exists(FIELD)).stream(findOptions).findFirst().orElse(null);
        return entitiy == null ? 1 : (entitiy.getId() + 1);
    }
}
