package entities;

import java.util.Date;

public interface Session {
    String getSessionId();
    Date getLastActive();
}
