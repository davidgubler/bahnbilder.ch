package entities;

public interface Country extends LocalizedEntity {
    int getId();

    String getCode();

    String getName(String lang);
}
