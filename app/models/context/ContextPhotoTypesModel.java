package models.context;

import com.google.inject.Inject;
import entities.PhotoType;
import models.PhotoTypesModel;
import utils.Context;

import java.util.stream.Stream;

public class ContextPhotoTypesModel extends ContextModel implements PhotoTypesModel {

    @Inject
    private PhotoTypesModel photoTypesModel;

    public ContextPhotoTypesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { photoTypesModel.clear(); return null; });
    }

    @Override
    public PhotoType create(int id, String nameDe, String nameEn) {
        return call(() -> photoTypesModel.create(id, nameDe, nameEn));
    }

    @Override
    public PhotoType get(Integer id) {
        return call(() -> photoTypesModel.get(id));
    }

    @Override
    public Stream<? extends PhotoType> getAll() {
        return call(() -> photoTypesModel.getAll());
    }
}
