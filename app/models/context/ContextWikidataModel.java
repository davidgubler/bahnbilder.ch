package models.context;

import com.google.inject.Inject;
import entities.Wikidata;
import models.WikidataModel;
import utils.Context;

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
}
