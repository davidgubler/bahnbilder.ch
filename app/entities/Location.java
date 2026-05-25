package entities;

public interface Location extends LocalizedEntity, NumIdEntity {
    int getId();

    String getName();

    default String getName(String lang) {
        return getName();
    }
}
