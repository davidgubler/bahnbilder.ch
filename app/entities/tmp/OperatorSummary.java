package entities.tmp;

import entities.ContextAwareEntity;
import entities.Search;
import entities.Wikidata;
import entities.Operator;
import utils.Context;

import java.time.LocalDate;

public class OperatorSummary implements ContextAwareEntity {
    private final Operator operator;
    private final Wikidata data;
    private final String logoUrl;
    private final String logoSrc;
    private final Integer inceptedYear;
    private final Integer dissolvedYear;
    private final String sourceRef;

    private Context context;

    public OperatorSummary(Operator operator, Wikidata data, String logoUrl, String logoSrc, Integer inceptedYear, Integer dissolvedYear, String sourceRef) {
        this.operator = operator;
        this.data = data;
        this.logoUrl = logoUrl;
        this.logoSrc = logoSrc;
        this.inceptedYear = inceptedYear;
        this.dissolvedYear = dissolvedYear;
        this.sourceRef = sourceRef;
    }

    public String getName(String lang) {
        return data.getName(lang);
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public Integer getInceptedYear() {
        return inceptedYear;
    }

    public Integer getDissolvedYear() {
        return dissolvedYear;
    }

    public Search getSearch() {
        LocalDate searchTo = getDissolvedYear() == null ? null : LocalDate.ofYearDay(getDissolvedYear() + 1, 1).plusDays(-1);
        return new Search(operator.getId(), data.getIncepted(), searchTo);
    }

    private Long count = null;

    public long getSearchCount() {
        if (count == null) {
            count = context.getPhotosModel().searchCount(getSearch());
        }
        return count;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    @Override
    public void inject(Context context) {
        this.context = context;
    }
}
