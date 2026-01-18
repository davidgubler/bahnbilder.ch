package entities;

public interface OperatorEra {
    String getName(String lang);

    String getLogoUrl();

    String getLogoSrc();

    Integer getInceptedYear();

    Integer getDissolvedYear();

    Search getSearch();

    long getSearchCount();

    String getSourceRef();
}
