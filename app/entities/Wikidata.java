package entities;

import java.time.LocalDate;

public interface Wikidata extends Comparable<Wikidata> {
    String getRef();

    String getData();

    boolean needsRefresh();

    LocalDate getIncepted();

    Integer getInceptedYear();

    LocalDate getDissolved();

    Integer getDissolvedYear();

    String getLogoUrl();

    String getLogoSrc();

    String getName(String lang);
}
