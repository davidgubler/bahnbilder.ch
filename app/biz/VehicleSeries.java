package biz;

import entities.User;
import entities.formdata.VehicleSeriesFormData;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class VehicleSeries implements CUDBusinessLogic<VehicleSeriesFormData> {

    @Override
    public void create(Context context, VehicleSeriesFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        entities.VehicleSeries existingVehicleSeries = context.getVehicleSeriesModel().getByName(data.name);
        if (existingVehicleSeries != null) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        entities.VehicleSeries vehicleClass = context.getVehicleSeriesModel().create(data);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " created " + vehicleClass);
    }

    @Override
    public void update(Context context, VehicleSeriesFormData data, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        InputUtils.validateString(data.name, "name", errors);
        entities.VehicleSeries existingVehicleSeries = context.getVehicleSeriesModel().getByName(data.name);
        if (existingVehicleSeries != null && !existingVehicleSeries.equals(data.entity)) {
            errors.put("name", ErrorMessages.ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getVehicleSeriesModel().update(data);

        // LOG
        BahnbilderLogger.info(context.getRequest(), user + " updated " + data.entity);
    }

    @Override
    public void delete(Context context, VehicleSeriesFormData data, User user) throws ValidationException {

    }
}
