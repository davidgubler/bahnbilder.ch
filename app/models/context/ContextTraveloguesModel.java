package models.context;

import com.google.inject.Inject;
import entities.Travelogue;
import entities.User;
import models.TraveloguesModel;
import utils.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class ContextTraveloguesModel extends ContextModel implements TraveloguesModel {

    @Inject
    private TraveloguesModel traveloguesModel;

    public ContextTraveloguesModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { traveloguesModel.clear(); return null; });
    }

    @Override
    public Travelogue create(int id, String title, Integer titlePhotoId, LocalDate date, String summary, String text, int userId) {
        return call(() -> traveloguesModel.create(id, title, titlePhotoId, date, summary, text, userId));
    }

    @Override
    public Travelogue create(String title, Integer titlePhotoId, LocalDate date, String summary, String text, User user) {
        return call(() -> traveloguesModel.create(title, titlePhotoId, date, summary, text, user));
    }

    @Override
    public void update(Travelogue travelogue, String title, Integer titlePhotoId, LocalDate date, String summary, String text) {
        call(() -> { traveloguesModel.update(travelogue, title, titlePhotoId, date, summary, text); return null; });
    }

    @Override
    public void delete(Travelogue travelogue) {
        call(() -> { traveloguesModel.delete(travelogue); return null; });
    }

    @Override
    public Travelogue get(Integer id) {
        return call(() -> traveloguesModel.get(id));
    }

    @Override
    public List<? extends Travelogue> getFeatured() {
        return call(() -> traveloguesModel.getFeatured());
    }

    @Override
    public Stream<? extends Travelogue> getAll() {
        return call(() -> traveloguesModel.getAll());
    }
}
