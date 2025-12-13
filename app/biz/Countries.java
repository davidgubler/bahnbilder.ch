package biz;

import entities.Country;
import entities.User;
import entities.formdata.CountryFormData;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class Countries implements CUDBusinessLogic<CountryFormData> {

    private BahnbilderLogger logger = new BahnbilderLogger(Countries.class);

    @Override
    public void create(Context context, CountryFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.nameDe, "nameDe", errors);
        Country existingCountry = context.getCountriesModel().getByName(data.nameDe);
        if (existingCountry != null) {
            errors.put("nameDe", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.nameEn, "nameEn", errors);
        existingCountry = context.getCountriesModel().getByName(data.nameEn);
        if (existingCountry != null) {
            errors.put("nameEn", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.code, "code", errors);
        existingCountry = context.getCountriesModel().getByCode(data.code);
        if (existingCountry != null) {
            errors.put("code", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        Country country = context.getCountriesModel().create(data);

        // LOG
        logger.info(context.getRequest(), user + " created " + country);
    }

    @Override
    public void update(Context context, CountryFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.nameDe, "nameDe", errors);
        Country existingCountry = context.getCountriesModel().getByName(data.nameDe);
        if (existingCountry != null && !existingCountry.equals(data.entity)) {
            errors.put("nameDe", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.nameEn, "nameEn", errors);
        existingCountry = context.getCountriesModel().getByName(data.nameEn);
        if (existingCountry != null && !existingCountry.equals(data.entity)) {
            errors.put("nameEn", ErrorMessages.ALREADY_EXISTS);
        }
        InputUtils.validateString(data.code, "code", errors);
        existingCountry = context.getCountriesModel().getByCode(data.code);
        if (existingCountry != null && !existingCountry.equals(data.entity)) {
            errors.put("code", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getCountriesModel().update(data.entity, data.nameDe, data.nameEn, data.code);

        // LOG
        logger.info(context.getRequest(), user + " edited " + data.entity);
    }

    @Override
    public void delete(Context context, CountryFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getCountriesModel().delete(data.entity);

        // LOG
        logger.info(context.getRequest(), user + " deleted " + data.entity);
    }
}
