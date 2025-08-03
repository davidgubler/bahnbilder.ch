package entities;

import java.util.List;

public interface Operator extends LocalizedEntity {
    int getId();

    String getAbbr();

    String getName();

    default String getName(String lang) {
        return getName();
    }

    List<String> getWikiDataIds();
}
