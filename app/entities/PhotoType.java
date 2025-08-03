package entities;

public interface PhotoType extends LocalizedEntity {
    int getId();

    String getName(String lang);
}
