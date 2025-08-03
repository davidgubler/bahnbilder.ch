package entities;

import java.util.List;
import java.util.Set;

public interface Keyword extends LocalizedEntity {
    int getId();

    String getName(String lang);

    Set<String> getLanguages();

    List<String> getLabels();
}
