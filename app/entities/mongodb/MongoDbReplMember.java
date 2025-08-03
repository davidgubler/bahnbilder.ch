package entities.mongodb;

import dev.morphia.annotations.Entity;
import utils.InputUtils;

@Entity(useDiscriminator = false)
public class MongoDbReplMember implements Comparable<MongoDbReplMember> {
    private int _id;
    private String name;
    private String stateStr;
    private Long uptime;
    private Long pingMs;
    private String lastHeartbeatMessage;

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getStateStr() {
        return stateStr.replace("(", "").replace(")", "");
    }

    public Long getUptime() {
        return uptime;
    }

    public Long getPingMs() {
        return pingMs;
    }

    public String getMessage() {
        if (lastHeartbeatMessage == null) {
            return null;
        }
        String[] parts = lastHeartbeatMessage.split("::");
        if (parts.length == 0) {
            return null;
        }
        return InputUtils.trimToNull(parts[parts.length - 1]);
    }

    @Override
    public int compareTo(MongoDbReplMember mongoDbReplMember) {
        return getName().compareTo(mongoDbReplMember.getName());
    }
}
