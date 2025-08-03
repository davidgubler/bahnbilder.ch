package models;

import entities.Wikidata;

public interface WikidataModel {
    void clear();

    Wikidata get(String ref);
}
