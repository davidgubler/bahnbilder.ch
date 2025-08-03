package entities.tmp;

import entities.Search;

public class OperatorSummary {
    private final String name;
    private final String logoUrl;
    private final String logoSrc;
    private final Integer inceptedYear;
    private final Integer dissolvedYear;
    private final Search search;
    private final long searchCount;
    private final String sourceRef;

    public OperatorSummary(String name, String logoUrl, String logoSrc, Integer inceptedYear, Integer dissolvedYear, Search search, long searchCount, String sourceRef) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.logoSrc = logoSrc;
        this.inceptedYear = inceptedYear;
        this.dissolvedYear = dissolvedYear;
        this.search = search;
        this.searchCount = searchCount;
        this.sourceRef = sourceRef;
    }

    public String getName() {
        return name;
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
        return search;
    }

    public long getSearchCount() {
        return searchCount;
    }

    public String getSourceRef() {
        return sourceRef;
    }
}
