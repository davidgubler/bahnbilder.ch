package biz;

import entities.User;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class Users {

    public User create(Context context, String email, String name, String password, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.isAdmin()) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        if (email == null) {
            errors.put("email", ErrorMessages.MISSING_VALUE);
        }
        if (name == null) {
            errors.put("name", ErrorMessages.MISSING_VALUE);
        }
        if (password == null) {
            errors.put("password", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        User createUser = context.getUsersModel().create(10, email, name, 1, password, false);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created " + createUser);
        return createUser;
    }

    public void update(Context context, User updateUser, String email, String name, String password, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.isAdmin()) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        if (email == null) {
            errors.put("email", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getUsersModel().update(updateUser, email, name, password);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " updated " + updateUser);
    }

    public void delete(Context context, User deleteUser, User user) {
        // ACCESS
        if (user == null || !user.isAdmin()) {
            throw new NotAllowedException();
        }

        // INPUT
        // nothing

        // BUSINESS
        context.getUsersModel().delete(deleteUser);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " deleted " + deleteUser);
    }

    public void ensureAdmin(Context context) {
        // ACCESS
        // nothing

        // INPUT
        // nothing

        // BUSINESS
        User createdAdmin = null;
        String pwd = null;
        if (context.getUsersModel().getAll().toList().isEmpty()) {
            pwd = Generator.generateSessionId();
            createdAdmin = context.getUsersModel().create(1, "admin@localhost", "Admin", 1, pwd, true);
        }

        // LOG
        if (createdAdmin != null) {
            BahnbilderLogger.info(context.getRequest(), "Created admin user " + createdAdmin + " with password '" + pwd + "'");
        }
    }
}
