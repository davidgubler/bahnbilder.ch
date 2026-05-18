package entities.mongodb;

import dev.morphia.annotations.*;
import entities.Session;
import entities.User;
import org.bson.types.ObjectId;
import utils.Generator;
import utils.SimplePBKDF2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static dev.morphia.utils.IndexType.TEXT;

@Entity(value = "users", useDiscriminator = false)
@Indexes({
        @Index(fields = @Field(value = "name", type = TEXT)),
        @Index(fields = @Field(value = "numId"), options =  @IndexOptions(unique = true)),
        @Index(fields = @Field(value = "email"), options =  @IndexOptions(unique = true)),
})
public class MongoDbUser implements MongoDbEntity, User {
    @Id
    private ObjectId _id;

    private int numId;

    private String email;

    private String name;

    private String passwordSalt;

    private String passwordHash;

    private int defaultLicenseId;

    private Boolean isAdmin = null;

    private List<MongoDbSession> sessions = new LinkedList<>();

    private Float searchScore;

    public MongoDbUser() {
        // constructor for Morphia
    }

    public MongoDbUser(int id, String email, String name, int defaultLicenseId, String password, boolean admin) {
        this.numId = id;
        this.email = email;
        this.name = name;
        this.defaultLicenseId = defaultLicenseId;
        setPassword(password);
        if (admin) {
            isAdmin = true;
        }
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    public void setPassword(String password) {
        passwordSalt = Generator.generatePasswordSaltHex();
        passwordHash = SimplePBKDF2.hash(passwordSalt, password);
    }

    @Override
    public int getId() {
        return numId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public List<? extends Session> getSessions() {
        return sessions;
    }

    public MongoDbSession startSession() {
        MongoDbSession mongoDbSession = new MongoDbSession();
        sessions.add(mongoDbSession);
        return mongoDbSession;
    }

    public void killSessions() {
        sessions = Collections.emptyList();
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean checkPassword(String password) {
        return SimplePBKDF2.hash(passwordSalt, password).equals(passwordHash);
    }

    @Override
    public boolean isAdmin() {
        return Boolean.TRUE.equals(isAdmin);
    }

    @Override
    public int getDefaultLicenseId() {
        return defaultLicenseId;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbUser that = (MongoDbUser) o;
        return numId == that.numId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numId);
    }

    public Float getSearchScore() {
        return searchScore;
    }
}
