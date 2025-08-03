package biz;

import com.google.inject.Inject;
import entities.User;
import services.Mail;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class Login {

    @Inject
    private Mail mail;

    public User login(Context context, String email, String password) throws ValidationException {
        // ACCESS
        // nothing

        // INPUT
        Map<String, String> errors = new HashMap<>();
        User user = context.getUsersModel().getByEmailAndPassword(email, password);
        if (user == null) {
            errors.put("email", ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
            errors.put("password", ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getUsersModel().startSession(user);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " logged in");

        return user;
    }

    public void logout(Context context, User user) {
        // ACCESS
        // nothing

        // INPUT
        // nothing

        // BUSINESS
        context.getUsersModel().killSessions(user);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " logged out");
    }

    public User lostPassword(Context context, String email, String lang) throws ValidationException {
        // ACCESS
        // nothing

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateEmail(email, "email", true, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        User user = context.getUsersModel().getByEmail(email);
        if (user != null) {
            Long ts = System.currentTimeMillis() / 1000l;
            String baseHost = context.getRequest().host();
            String baseUrl = (context.getRequest().secure() ? "https://" : "http://") + baseHost;
            mail.lostPassword(user, baseHost, baseUrl, lang, ts, lostPasswordSignature(ts, user.getEmail()));
        }

        // LOG
        BahnbilderLogger.info(context.getRequest(), "Requested password recovery email for " + email + ", " + (user == null ? "not sent (user unknown)" : "sent"));

        return user;
    }

    public String lostPasswordSignature(Long ts, String email) {
        return new SimpleHMAC(Config.MAC_SIGNING_KEY).sign("lostPassword" + "|" + ts + "|" + email);
    }

    public User linkLogin(Context context, Long ts, String email, String sig) throws ValidationException {
        // ACCESS
        // nothing

        // INPUT
        Map<String, String> errors = new HashMap<>();
        User user = context.getUsersModel().getByEmail(email);
        if (user == null) {
            errors.put("email", ErrorMessages.INVALID_EMAIL);
        }
        if (System.currentTimeMillis() / 1000L - ts > 60L*10L) {
            errors.put("ts", ErrorMessages.INVALID_VALUE);
        }
        if (!lostPasswordSignature(ts, email).equals(sig)) {
            errors.put("sig", ErrorMessages.INVALID_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getUsersModel().startSession(user);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " logged in via email link");

        return user;
    }

    public String newPw(Context context, Long ts, String email, String sig) throws ValidationException {
        // ACCESS
        // nothing

        // INPUT
        Map<String, String> errors = new HashMap<>();
        User user = context.getUsersModel().getByEmail(email);
        if (user == null) {
            errors.put("email", ErrorMessages.INVALID_EMAIL);
        }
        if (System.currentTimeMillis() / 1000L - ts > 60L*10L) {
            errors.put("ts", ErrorMessages.INVALID_VALUE);
        }
        if (!lostPasswordSignature(ts, email).equals(sig)) {
            errors.put("sig", ErrorMessages.INVALID_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        String password = Generator.generatePassword();
        context.getUsersModel().updatePassword(user, password);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created a new password");

        return password;
    }
}
