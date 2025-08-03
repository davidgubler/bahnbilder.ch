package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import entities.Session;
import entities.User;
import entities.mongodb.MongoDbSession;
import entities.mongodb.MongoDbUser;
import models.UsersModel;
import play.mvc.Http;
import utils.InputUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MongoDbUsersModel extends MongoDbModel<MongoDbUser> implements UsersModel {

    private static final long LAST_ACTIVE_REFRESH_MS = 1000L * 60L * 5L;

    @Override
    public User getByEmailAndPassword(String email, String password) {
        User user = getByEmail(email);
        if (user == null) {
            return null;
        }
        if (!user.checkPassword(password)) {
            return null;
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        return query().filter(Filters.eq("email", email)).first();
    }

    @Override
    public User getFromRequest(Http.Request request) {
        Http.Cookie sessionIdCookie = request.cookies().get("sessionId").orElse(null);
        if (sessionIdCookie == null) {
            return null;
        }
        String sessionId = InputUtils.trimToNull(sessionIdCookie.value());
        if (sessionId == null) {
            return null;
        }
        User user = query().filter(Filters.eq("sessions.sessionId", sessionId)).first();
        if (user == null) {
            return null;
        }
        String method = request.method();
        if (!"GET".equals(method) && !"HEAD".equals(method)) {
            // We require the csrfToken to be sent as a form value whenever methods other than GET or HEAD are used
            Http.Cookie csrfTokenCookie = request.cookies().get("csrfToken").orElse(null);
            if (csrfTokenCookie == null) {
                return null;
            }
            String cookieCsrfToken = InputUtils.trimToNull(csrfTokenCookie.value());

            String formCsrfToken = null;
            try {
                String contentType = request.header(Http.HeaderNames.CONTENT_TYPE).orElse("");
                if (contentType.startsWith("multipart/form-data;")) {
                    formCsrfToken = InputUtils.trimToNull(request.body().asMultipartFormData().asFormUrlEncoded().get("csrfToken"));
                } else {
                    formCsrfToken = InputUtils.trimToNull(request.body().asFormUrlEncoded().get("csrfToken"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // too bad
            }


            if (cookieCsrfToken == null || formCsrfToken == null || !formCsrfToken.equals(cookieCsrfToken)) {
                return null;
            }
        }
        Session session = user.getSessions().stream().filter(s -> s.getSessionId().equals(sessionId)).collect(Collectors.toList()).get(0);
        if (session.getLastActive().getTime() < (new Date().getTime() - LAST_ACTIVE_REFRESH_MS)) {
            ((MongoDbSession)session).setLastActive(new Date());
            query().filter(Filters.eq("sessions.sessionId", sessionId)).update(new UpdateOptions(), UpdateOperators.set("sessions.$.lastActive", session.getLastActive()));
        }
        return user;
    }

    @Override
    public User create(int id, String email, String name, int defaultLicenseId, String password, boolean admin) {
        MongoDbUser mongoDbUser = new MongoDbUser(id, email, name, defaultLicenseId, password, admin);
        mongoDb.getDs().save(mongoDbUser);
        return mongoDbUser;
    }

    @Override
    public void update(User user, String email, String name, String password) {
        MongoDbUser mongoDbUser = (MongoDbUser)user;
        ArrayList<UpdateOperator> ops = new ArrayList<>();
        mongoDbUser.setEmail(email);
        ops.add(UpdateOperators.set("email", mongoDbUser.getEmail()));
        mongoDbUser.setName(name);
        ops.add(UpdateOperators.set("name", mongoDbUser.getName()));
        if (password != null) {
            mongoDbUser.setPassword(password);
            ops.add(UpdateOperators.set("passwordSalt", mongoDbUser.getPasswordSalt()));
            ops.add(UpdateOperators.set("passwordHash", mongoDbUser.getPasswordHash()));
        }
        query(user).update(new UpdateOptions(), ops.toArray(UpdateOperator[]::new));
    }

    @Override
    public void updatePassword(User user, String password) {
        MongoDbUser mongoDbUser = (MongoDbUser)user;
        ArrayList<UpdateOperator> ops = new ArrayList<>();
        mongoDbUser.setPassword(password);
        ops.add(UpdateOperators.set("passwordSalt", mongoDbUser.getPasswordSalt()));
        ops.add(UpdateOperators.set("passwordHash", mongoDbUser.getPasswordHash()));
        query(user).update(new UpdateOptions(), ops.toArray(UpdateOperator[]::new));
    }

    @Override
    public void startSession(User user) {
        MongoDbUser mongoDbUser = ((MongoDbUser)user);
        MongoDbSession mongoDbSession = mongoDbUser.startSession();
        List<UpdateOperator> ops = new LinkedList<>();
        if (mongoDbUser.getSessions().size() == 1) {
            ops.add(UpdateOperators.set("sessions", Arrays.asList(mongoDbSession)));
        } else {
            ops.add(UpdateOperators.push("sessions", mongoDbSession));
        }
        query(user).update(new UpdateOptions(), ops.toArray(UpdateOperator[]::new));
    }

    @Override
    public void killSessions(User user) {
        MongoDbUser mongoDbUser = ((MongoDbUser)user);
        mongoDbUser.killSessions();
        query(user).update(new UpdateOptions(), UpdateOperators.unset("sessions"));
    }
}
