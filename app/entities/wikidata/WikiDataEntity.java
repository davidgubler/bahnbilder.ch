package entities.wikidata;


import java.util.List;
import java.util.Map;

public class WikiDataEntity {
    public String title;
    public Map<String, WikiDataLabel> labels;
    public Map<String, List<WikiDataClaim>> claims;
}
