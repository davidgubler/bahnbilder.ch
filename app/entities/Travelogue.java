package entities;

import java.time.LocalDate;

public interface Travelogue extends Comparable<Travelogue> {
    int getId();

    LocalDate getDate();

    int getUserId();

    User getUser();

    String getTitle();

    Integer getTitlePhotoId();

    Photo getTitlePhoto();

    String getSummary();

    String getText();

    String toHtml(String lang);

    String toBBCode(String lang);

    @Override
    default int compareTo(Travelogue other) {
        if (getDate() == null || other.getDate() == null || getDate().equals(other.getDate())) {
            return Integer.compare(getId(), other.getId());
        }
        return getDate().compareTo(other.getDate());
    }
}
