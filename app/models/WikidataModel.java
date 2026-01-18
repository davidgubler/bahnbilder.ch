package models;

import entities.Wikidata;

import java.util.Collection;
import java.util.List;

public interface WikidataModel {
    void clear();

    Wikidata get(String ref);

    List<Wikidata> get(Collection<String> ids);
}
