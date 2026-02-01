package models.mongodb;

import com.mongodb.MongoWriteException;
import dev.morphia.UpdateOptions;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import entities.Travelogue;
import entities.User;
import entities.mongodb.MongoDbTravelogue;
import models.TraveloguesModel;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MongoDbTraveloguesModel extends MongoDbModel<MongoDbTravelogue> implements TraveloguesModel {
    @Override
    public Travelogue create(int id, String title, Integer titlePhotoId, LocalDate date, String summary, String text, int userId) {
        Travelogue travelogue = new MongoDbTravelogue(id, title, titlePhotoId, date, summary, text, userId);
        mongoDb.getDs().save(travelogue);
        return travelogue;
    }

    @Override
    public Travelogue create(String title, Integer titlePhotoId, LocalDate date, String summary, String text, User user) {
        // FIXME this should probably be implemented with a lambda in the MongoDbModel
        for (int i = 0; i < 10; i++) {
            try {
                return create(getNextNumId(), title, titlePhotoId, date, summary, text, user.getId());
            } catch (MongoWriteException e) {
                // Likely a duplicate key error. Try again.
            }
        }
        return null;
    }

    @Override
    public void update(Travelogue travelogue, String title, Integer titlePhotoId, LocalDate date, String summary, String text) {
        List<UpdateOperator> updates = new LinkedList<>();
        if (!Objects.equals(title, travelogue.getTitle())) {
            ((MongoDbTravelogue)travelogue).setTitle(title);
            updates.add(UpdateOperators.set("title", title));
        }
        if (!Objects.equals(titlePhotoId, travelogue.getTitlePhotoId())) {
            ((MongoDbTravelogue)travelogue).setTitlePhotoId(titlePhotoId);
            updates.add(UpdateOperators.set("titlePhotoId", titlePhotoId));
        }
        if (!Objects.equals(date, travelogue.getDate())) {
            ((MongoDbTravelogue)travelogue).setDate(date);
            updates.add(UpdateOperators.set("date", date));
        }
        if (!Objects.equals(summary, travelogue.getSummary())) {
            ((MongoDbTravelogue)travelogue).setSummary(summary);
            updates.add(UpdateOperators.set("summary", summary));
        }
        if (!Objects.equals(text, travelogue.getText())) {
            ((MongoDbTravelogue)travelogue).setText(text);
            updates.add(UpdateOperators.set("text", text));
        }
        if (!updates.isEmpty()) {
            query(travelogue).update(new UpdateOptions(), updates.toArray(new UpdateOperator[updates.size()]));
        }
    }

    @Override
    public void delete(Travelogue travelogue) {
        super.delete(travelogue);
    }

    @Override
    public List<? extends Travelogue> getFeatured() {
        return query().filter(Filters.ne("title", null), Filters.ne("titlePhotoId", null)).stream(new FindOptions().projection().include("numId", "title", "summary", "titlePhotoId").sort(Sort.descending("numId")).limit(3)).toList();
    }

    @Override
    public MongoDbTravelogue get(Integer id) {
        return super.get(id);
    }
}
