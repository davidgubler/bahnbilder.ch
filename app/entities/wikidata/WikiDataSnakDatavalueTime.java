package entities.wikidata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = WikiDataSnakDatavalueTime.class)
public class WikiDataSnakDatavalueTime extends WikiDataSnakDatavalue {
    public WikiDataSnakDatavalueTimeTime value;

    public WikiDataSnakDatavalueTime() {
        super();
    }

    public WikiDataSnakDatavalueTime(String type) {
        super(type);
    }
}