package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import entities.*;
import utils.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity(useDiscriminator = false)
public class MongoDbOperatorEra implements OperatorEra {
    private Map<String, String> names = new HashMap<>();
    private String logoUrl;
    private String logoSrc;
    private Integer inceptedYear;
    private Integer dissolvedYear;
    private String sourceRef;

    @Transient
    private Context context;

    @Transient
    private Operator operator;

    public MongoDbOperatorEra() {
        // dummy for Morphia
    }

    public MongoDbOperatorEra(String nameDe, String nameEn, String logoUrl, String logoSrc, Integer inceptedYear, Integer dissolvedYear, String sourceRef) {
        this.names.put("de", nameDe);
        this.names.put("en", nameEn);
        this.logoUrl = logoUrl;
        this.logoSrc = logoSrc;
        this.inceptedYear = inceptedYear;
        this.dissolvedYear = dissolvedYear;
        this.sourceRef = sourceRef;
    }

    public void inject(Context context, Operator operator) {
        this.context = context;
        this.operator = operator;
    }

    @Override
    public String getName(String lang) {
        String name = names.get(lang);
        return name == null ? operator.getName() : name;
    }

    @Override
    public String getLogoUrl() {
        return logoUrl;
    }

    @Override
    public String getLogoSrc() {
        return logoSrc;
    }

    @Override
    public Integer getInceptedYear() {
        return inceptedYear;
    }

    @Override
    public Integer getDissolvedYear() {
        return dissolvedYear;
    }

    @Override
    public Search getSearch() {
        LocalDate searchFrom = getInceptedYear() == null ? null : LocalDate.ofYearDay(getInceptedYear(), 1);
        LocalDate searchTo = getDissolvedYear() == null ? null : LocalDate.ofYearDay(getDissolvedYear() + 1, 1).plusDays(-1);
        return new Search(operator.getId(), searchFrom, searchTo);
    }

    @Transient
    private Long count = null;

    @Override
    public long getSearchCount() {
        if (count == null) {
            count = context.getPhotosModel().searchCount(getSearch());
        }
        return count;
    }

    @Override
    public String getSourceRef() {
        return sourceRef;
    }
}
