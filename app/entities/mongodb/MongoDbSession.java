package entities.mongodb;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Indexed;
import entities.Session;
import utils.Generator;

import java.util.Date;

@Embedded(useDiscriminator = false)
public class MongoDbSession implements Session {
    @Indexed
    private String sessionId;

    private Date lastActive;

    public MongoDbSession() {
        this.sessionId = Generator.generateSessionId();
        this.lastActive = new Date();
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }
}
