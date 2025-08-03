package models.context;

import com.google.inject.Inject;
import entities.User;
import models.UsersModel;
import play.mvc.Http;
import utils.Context;

import java.util.stream.Stream;

public class ContextUsersModel extends ContextModel implements UsersModel {

    @Inject
    private UsersModel usersModel;

    public ContextUsersModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { usersModel.clear(); return null; });
    }

    @Override
    public Stream<? extends User> getAll() {
        return call(() -> usersModel.getAll());
    }

    @Override
    public User getByEmailAndPassword(String email, String password) {
        return call(() -> usersModel.getByEmailAndPassword(email, password));
    }

    @Override
    public User getByEmail(String email) {
        return call(() -> usersModel.getByEmail(email));
    }

    @Override
    public User get(Integer id) {
        return call(() -> usersModel.get(id));
    }

    @Override
    public User getFromRequest(Http.Request request) {
        return call(() -> usersModel.getFromRequest(request));
    }

    @Override
    public User create(int id, String email, String name, int defaultLicenseId, String password, boolean admin) {
        return call(() -> usersModel.create(id, email, name, defaultLicenseId, password, admin));
    }

    @Override
    public void update(User user, String email, String name, String password) {
        call(() -> { usersModel.update(user, email, name, password); return null; });
    }

    @Override
    public void updatePassword(User user, String password) {
        call(() -> { usersModel.updatePassword(user, password); return null; });
    }

    @Override
    public void delete(Object entity) {
        call(() -> { usersModel.delete(entity); return null; });
    }

    @Override
    public void startSession(User user) {
        call(() -> { usersModel.startSession(user); return null; });
    }

    @Override
    public void killSessions(User user) {
        call(() -> { usersModel.killSessions(user); return null; });
    }
}
