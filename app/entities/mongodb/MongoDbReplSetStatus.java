package entities.mongodb;

import dev.morphia.annotations.Entity;

import java.util.Collections;
import java.util.List;

@Entity
public class MongoDbReplSetStatus {
    private String set;
    private int myState;
    private List<MongoDbReplMember> members;

    public boolean isConnectedToPrimary() {
        return myState == 1;
    }

    @Override
    public String toString() {
        String s = "Replica Set " + set + "\n";
        s += "members:\n";
        Collections.sort(members);
        for (MongoDbReplMember m : members) {
            s += " " + m.getId() + ": " + m.getName() + "\n";
            s += "    state:   " + m.getStateStr() + "\n";
            s += "    uptime:  " + m.getUptime() + "s\n";
            if (m.getPingMs() != null) {
                s += "    ping:    " + m.getPingMs() + "ms\n";
            }
            if (m.getMessage() != null) {
                s += "    message: " + m.getMessage() + "\n";
            }
        }
        return s;
    }

    public String toStringAnon() {
        String s = toString();
        int i = 1;
        for (MongoDbReplMember m : members) {
            s = s.replace(m.getName(), "node" + i++);
        }
        return s;
    }
}
