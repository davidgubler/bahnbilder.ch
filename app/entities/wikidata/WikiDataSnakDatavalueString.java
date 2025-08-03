package entities.wikidata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = WikiDataSnakDatavalueString.class)
public class WikiDataSnakDatavalueString extends WikiDataSnakDatavalue {
    public WikiDataSnakDatavalueString() {
        super();
        // dummy for deserializer
    }

    public WikiDataSnakDatavalueString(String type) {
        super(type);
    }

    public String value;
}
