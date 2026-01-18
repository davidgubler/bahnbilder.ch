package entities.mongodb;

import dev.morphia.annotations.*;
import entities.ContextAwareEntity;
import entities.Operator;
import entities.OperatorEra;
import org.bson.types.ObjectId;
import utils.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(value = "operators", useDiscriminator = false)
public class MongoDbOperator implements ContextAwareEntity, MongoDbEntity, Operator {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private String abbr;

    private String name;

    private List<String> wikiDataIds;

    private List<MongoDbOperatorEra> eras = new ArrayList();

    private Instant erasLastRefresh;

    @Transient
    private Context context;

    public MongoDbOperator() {
        // dummy for Morphia
    }

    public MongoDbOperator(int id, String name, String abbr, List<String> wikiDataIds) {
        this.numId = id;
        this.name = name;
        this.abbr = abbr;
        this.wikiDataIds = wikiDataIds;
    }


    @Override
    public void inject(Context context) {
        this.context = context;
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public int getId() {
        return numId;
    }

    @Override
    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getName(String lang, LocalDateTime date) {
        if (date == null) {
            return getName();
        }
        for (OperatorEra era : getEras()) {
            if ((era.getInceptedYear() == null || era.getInceptedYear() <= date.getYear()) && (era.getDissolvedYear() == null || era.getDissolvedYear() >= date.getYear())) {
                return era.getName(lang);
            }
        }
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getWikiDataIds() {
        return wikiDataIds == null ? Collections.emptyList() : wikiDataIds;
    }

    @Override
    public List<? extends OperatorEra> getEras() {
        if (erasLastRefresh == null || erasLastRefresh.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
            context.getOperatorsModel().updateEras(this, ids -> context.getWikidataModel().get(ids));
        }
        eras.forEach(e -> e.inject(context, this));
        return eras;
    }

    public void setErasLastRefresh(Instant erasLastRefresh) {
        this.erasLastRefresh = erasLastRefresh;
    }

    public void setWikiDataIds(List<String> wikiDataIds) {
        this.wikiDataIds = wikiDataIds;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbOperator that = (MongoDbOperator) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_id);
    }
}
