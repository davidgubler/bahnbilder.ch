package models.context;

import com.google.inject.Inject;
import entities.License;
import models.LicensesModel;
import utils.Context;

import java.util.List;

public class ContextLicensesModel extends ContextModel implements LicensesModel {

    @Inject
    private LicensesModel licensesModel;

    public ContextLicensesModel(Context context) {
        this.context = context;
    }

    @Override
    public License get(Integer id) {
        return call(() -> licensesModel.get(id));
    }

    @Override
    public List<? extends License> getAll() {
        return call(() -> licensesModel.getAll());
    }
}
