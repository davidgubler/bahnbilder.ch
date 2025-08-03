package entities.wikidata;

import java.util.List;
import java.util.Map;

public class WikiDataClaim {
    // P571 -> inception
    public WikiDataSnak mainsnak;
    public Map<String, List<WikiDataSnak>> qualifiers;
}
