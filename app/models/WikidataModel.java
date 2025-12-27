package models;

import entities.Operator;
import entities.Wikidata;
import entities.tmp.OperatorSummary;

import java.util.List;

public interface WikidataModel {
    void clear();

    Wikidata get(String ref);

    List<OperatorSummary> getOperatorSummaries(Operator operator);
}
