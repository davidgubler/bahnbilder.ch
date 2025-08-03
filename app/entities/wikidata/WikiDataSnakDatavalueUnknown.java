package entities.wikidata;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = WikiDataSnakDatavalueUnknown.class)
public class WikiDataSnakDatavalueUnknown extends WikiDataSnakDatavalue {
    public WikiDataSnakDatavalueUnknown() {
        super();
    }

    public WikiDataSnakDatavalueUnknown(String type) {
        super(type);
    }
}