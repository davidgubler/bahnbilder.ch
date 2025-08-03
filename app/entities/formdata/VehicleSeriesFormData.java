package entities.formdata;

import entities.VehicleSeries;
import play.mvc.Http;
import utils.InputUtils;

import java.util.Map;

public class VehicleSeriesFormData extends FormData<VehicleSeries> {
    public final String name;

    public VehicleSeriesFormData(Http.Request request, String returnUrl, VehicleSeries vehicleSeries) {
        super(request, returnUrl, vehicleSeries);
        if ("POST".equals(request.method())) {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            name = InputUtils.trimToNull(data.get("name"));
        } else {
            name = vehicleSeries == null ? null : vehicleSeries.getName();
        }
    }
}
