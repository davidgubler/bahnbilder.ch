package biz;

import entities.Travelogue;
import entities.User;
import utils.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Travelogues {

    public Travelogue create(Context context, String title, String titlePhoto, String dateStr, String summary, String text, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(title, "title", errors);
        Integer titlePhotoId = InputUtils.toInt(titlePhoto);
        if (titlePhoto != null && context.getPhotosModel().get(titlePhotoId) == null) {
            errors.put("titlePhoto", ErrorMessages.INVALID_PHOTO_ID);
        }
        LocalDate date = InputUtils.validateDate(dateStr, "date", true, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        Travelogue travelogue = context.getTraveloguesModel().create(title, titlePhotoId, date, summary, text, user);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created travelogue " + travelogue);

        return travelogue;
    }

    public Travelogue update(Context context, Travelogue travelogue, String title, String titlePhoto, String dateStr, String summary, String text, User user) throws ValidationException {
        // ACCESS
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(title, "title", errors);
        Integer titlePhotoId = InputUtils.toInt(titlePhoto);
        if (titlePhoto != null && context.getPhotosModel().get(titlePhotoId) == null) {
            errors.put("titlePhoto", ErrorMessages.INVALID_PHOTO_ID);
        }
        LocalDate date = InputUtils.validateDate(dateStr, "date", true, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getTraveloguesModel().update(travelogue, title, titlePhotoId, date, summary, text);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " updated travelogue " + travelogue);

        return travelogue;
    }

    public Travelogue delete(Context context, Travelogue travelogue, User user) {
        // ACCESS
        if (user == null || !user.canEdit(travelogue)) {
            throw new NotAllowedException();
        }

        // INPUT

        // BUSINESS
        context.getTraveloguesModel().delete(travelogue);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " deleted travelogue " + travelogue);

        return travelogue;
    }
}
