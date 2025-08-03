package entities.formdata;

import entities.VehicleClass;
import play.mvc.Http;
import utils.InputUtils;

import java.util.Map;

public class VehicleClassFormData extends FormData<VehicleClass> {
    public final String name;
    public final String nameNumberFormat;
    public final Integer vehicleSeriesId;
    public final Integer vehicleTypeId;
    public final Integer vehiclePropulsionId;

    public VehicleClassFormData(Http.Request request, String returnUrl, VehicleClass vehicleClass) {
        super(request, returnUrl, vehicleClass);
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            name = InputUtils.trimToNull(data.get("name"));
            nameNumberFormat = InputUtils.trimToNull(data.get("nameNumberFormat"));
            vehicleSeriesId = InputUtils.toInt(data.get("vehicleSeries"));
            vehicleTypeId = InputUtils.toInt(data.get("vehicleType"));
            vehiclePropulsionId = InputUtils.toInt(data.get("vehiclePropulsion"));
        } else {
            name = vehicleClass == null ? null : vehicleClass.getName();
            nameNumberFormat = vehicleClass == null ? null : vehicleClass.getNameNumberFormat();
            vehicleSeriesId = vehicleClass == null ? null : vehicleClass.getVehicleSeriesId();
            vehicleTypeId = vehicleClass == null ? null : vehicleClass.getVehicleTypeId();
            vehiclePropulsionId = vehicleClass == null ? null : vehicleClass.getVehiclePropulsionId();
        }
    }
}
