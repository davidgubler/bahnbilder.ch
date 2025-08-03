package models;

import entities.User;
import play.mvc.Http;

import java.util.stream.Stream;

public interface UsersModel {
    void clear();

    Stream<? extends User> getAll();

    User getByEmailAndPassword(String email, String password);

    User getByEmail(String email);

    User get(Integer id);

    User getFromRequest(Http.Request request);

    User create(int id, String email, String name, int defaultLicenseId, String password, boolean admin);

    void update(User user, String email, String name, String password);

    void updatePassword(User user, String password);

    void delete(Object entity);

    void startSession(User user);

    void killSessions(User user);
}
