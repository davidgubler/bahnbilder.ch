package entities.wikidata;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class WikiDataSnakDatavalueTimeTime {
    public String time;

    public ZonedDateTime getZonedDateTime() {
        String t = time;
        if (t.startsWith("+")) {
            t = t.substring(1);
        }
        // When the exact date is not known we may get a year with month or date set to 0. This isn't parseable. Assume Jan 1st.
        if (t.contains("-00-00T")) {
            t = t.replace("-00-00T", "-01-01T");
        } else if (t.contains("-00T")) {
            t = t.replace("-00T", "-01T");
        }
        return ZonedDateTime.parse(t);
    }

    public LocalDate getDate() {
        return getZonedDateTime().toLocalDate();
    }
}
