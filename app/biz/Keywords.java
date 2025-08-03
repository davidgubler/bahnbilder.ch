package biz;

import entities.Keyword;
import entities.User;
import entities.formdata.KeywordFormData;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keywords implements CUDBusinessLogic<KeywordFormData> {

    @Override
    public void create(Context context, KeywordFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.nameDe, "nameDe", errors);
        Keyword existingKeyword = context.getKeywordsModel().getByName(data.nameDe);
        if (existingKeyword != null) {
            errors.put("nameDe", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.nameEn, "nameEn", errors);
        existingKeyword = context.getKeywordsModel().getByName(data.nameEn);
        if (existingKeyword != null) {
            errors.put("nameEn", ErrorMessages.ALREADY_EXISTS);
        }
        List<String> labels = InputUtils.parseTokens(data.labels);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        Keyword keyword = context.getKeywordsModel().create(data, labels);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created " + keyword);
    }

    @Override
    public void update(Context context, KeywordFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.nameDe, "nameDe", errors);
        Keyword existingKeyword = context.getKeywordsModel().getByName(data.nameDe);
        if (existingKeyword != null && !existingKeyword.equals(data.entity)) {
            errors.put("nameDe", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.nameEn, "nameEn", errors);
        existingKeyword = context.getKeywordsModel().getByName(data.nameEn);
        if (existingKeyword != null && !existingKeyword.equals(data.entity)) {
            errors.put("nameEn", ErrorMessages.ALREADY_EXISTS);
        }
        List<String> labels = InputUtils.parseTokens(data.labels);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getKeywordsModel().update(data, labels);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " updated " + data.entity);
    }

    @Override
    public void delete(Context context, KeywordFormData data, User user) throws ValidationException {

    }
}
