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

    default String getLogoUrl() {
        List<? extends OperatorEra> eras = getEras();
        return eras == null || eras.isEmpty() ? null : eras.get(0).getLogoUrl();
    }
}
