package entities;

public interface Location extends LocalizedEntity {
    int getId();

    String getName();

    default String getName(String lang) {
        return getName();
    }
}
