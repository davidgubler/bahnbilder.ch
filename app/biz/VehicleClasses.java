package biz;

import entities.User;
import entities.VehicleClass;
import entities.formdata.VehicleClassFormData;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class VehicleClasses implements CUDBusinessLogic<VehicleClassFormData> {

    private BahnbilderLogger logger = new BahnbilderLogger(VehicleClasses.class);

    @Override
    public void create(Context context, VehicleClassFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        VehicleClass existingVehicleClass = context.getVehicleClassesModel().getByName(data.name);
        if (existingVehicleClass != null) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        VehicleClass vehicleClass = context.getVehicleClassesModel().create(data);

        // LOG
        logger.info(context.getRequest(), user + " created " + vehicleClass);
    }

    @Override
    public void update(Context context, VehicleClassFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        VehicleClass existingVehicleClass = context.getVehicleClassesModel().getByName(data.name);
        if (existingVehicleClass != null && !existingVehicleClass.equals(data.entity)) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getVehicleClassesModel().update(data);

        // LOG
        logger.info(context.getRequest(), user + " updated " + data.entity);
    }

    @Override
    public void delete(Context context, VehicleClassFormData data, User user) throws ValidationException {

    }
}
