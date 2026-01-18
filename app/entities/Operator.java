package entities;

import java.time.LocalDateTime;
import java.util.List;

public interface Operator extends LocalizedEntity {
    int getId();

    String getAbbr();

    String getName();

    default String getName(String lang) {
        return getName();
    }

    String getName(String lang, LocalDateTime date);

    List<String> getWikiDataIds();

    List<? extends OperatorEra> getEras();
}
