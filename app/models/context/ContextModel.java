package models.context;

import entities.ContextAwareEntity;
import play.libs.F;
import utils.BahnbilderLogger;
import utils.Context;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class ContextModel {
    private int calls;
    private int ms;
    protected Context context;

    public F.Tuple<Integer, Integer> getCalls() {
        return new F.Tuple<>(calls, ms);
    }

    @SuppressWarnings("unchecked")
    protected <T> T call(Supplier<T> f) {
        long startTime = System.currentTimeMillis();
        try {
            T x = f.get();
            if (x instanceof ContextAwareEntity) {
                inject((ContextAwareEntity)x);
            } else if (x instanceof List) {
                List l = (List)x;
                if (!l.isEmpty() && l.get(0) instanceof ContextAwareEntity) {
                    inject(l);
                }
            } else if (x instanceof Set) {
                Set s = (Set)x;
                if (!s.isEmpty() && s.iterator().next() instanceof ContextAwareEntity) {
                    inject(s);
                }
            } else if (x instanceof Stream) {
                Stream s = (Stream)x;
                return (T)s.map(e -> {
                    if (e instanceof ContextAwareEntity) {
                        inject((ContextAwareEntity)e);
                    }
                    return e;
                });
            }

            return x;
        } catch (Exception e) {
            BahnbilderLogger.error(context.getRequest(), e);
            throw e;
        } finally {
            calls++;
            ms += System.currentTimeMillis() - startTime;
        }
    }

    protected <T extends ContextAwareEntity> T inject(T entity) {
        if (entity != null) {
            entity.inject(context);
        }
        return entity;
    }

    protected <T extends ContextAwareEntity> List<T> inject(List<T> entities) {
        entities.stream().forEach(e -> e.inject(context));
        return entities;
    }

    protected <T extends ContextAwareEntity> Set<T> inject(Set<T> entities) {
        entities.stream().forEach(e -> e.inject(context));
        return entities;
    }
}
