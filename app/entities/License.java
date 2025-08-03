package entities;

public interface License extends Comparable<License> {
    int getId();

    String getName();

    String getLogo(String lang);

    default int compareTo(License license) {
        return Long.compare(getId(), license.getId());
    }
}
