package models.context;

import com.google.inject.Inject;
import entities.Operator;
import entities.Wikidata;
import entities.tmp.OperatorSummary;
import models.WikidataModel;
import utils.Context;

import java.util.List;

public class ContextWikidataModel extends ContextModel implements WikidataModel {

    @Inject
    private WikidataModel wikidataModel;

    public ContextWikidataModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { wikidataModel.clear(); return null; });
    }

    @Override
    public Wikidata get(String ref) {
        return call(() -> wikidataModel.get(ref));
    }

    @Override
    public List<OperatorSummary> getOperatorSummaries(Operator operator) {
        return call(() -> wikidataModel.getOperatorSummaries(operator));
    }
}
