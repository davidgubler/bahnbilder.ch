package entities.wikidata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = WikiDataSnakDeserializer.class)
public abstract class WikiDataSnakDatavalue {
    private String type;

    public WikiDataSnakDatavalue() {
        // dummy for deserializer
    }

    public WikiDataSnakDatavalue(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
