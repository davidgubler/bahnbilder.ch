package entities;

public interface Country extends LocalizedEntity, NumIdEntity {
    int getId();

    String getCode();

    String getName(String lang);
}
