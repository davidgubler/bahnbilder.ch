package biz;

import com.google.inject.Inject;
import entities.Operator;
import entities.User;
import entities.formdata.OperatorFormData;
import models.OperatorsModel;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operators implements CUDBusinessLogic<OperatorFormData> {

    @Override
    public void create(Context context, OperatorFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        Operator existingOperator = context.getOperatorsModel().getByName(data.name);
        if (existingOperator != null) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.abbr, "abbr", errors);
        List<String> wikiDataIds = InputUtils.parseTokens(data.wikiData);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        Operator operator = context.getOperatorsModel().create(data.name, data.abbr, wikiDataIds);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created " + operator);
    }

    @Override
    public void update(Context context, OperatorFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        Operator existingOperator = context.getOperatorsModel().getByName(data.name);
        if (existingOperator != null && !existingOperator.equals(data.entity)) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.abbr, "abbr", errors);
        List<String> wikiDataIds = InputUtils.parseTokens(data.wikiData);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getOperatorsModel().update(data.entity, data.name, data.abbr, wikiDataIds);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " updated " + data.entity);
    }

    @Override
    public void delete(Context context, OperatorFormData data, User user) throws ValidationException {

    }
}
