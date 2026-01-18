package models.context;

import com.google.inject.Inject;
import entities.Wikidata;
import models.WikidataModel;
import utils.Context;

import java.util.Collection;
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
    public List<Wikidata> get(Collection<String> ids) {
        return call(() -> wikidataModel.get(ids));
    }
}
