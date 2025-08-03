package entities;

import java.time.LocalDate;
import java.util.List;

public interface Views {
    List<String> getIps();

    int getPhotoId();

    LocalDate getDate();
}
