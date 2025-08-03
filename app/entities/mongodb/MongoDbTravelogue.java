package entities.mongodb;

import dev.morphia.annotations.*;
import entities.ContextAwareEntity;
import entities.Photo;
import entities.Travelogue;
import entities.User;
import org.bson.types.ObjectId;
import utils.Context;
import utils.MarkupParser;

import java.time.LocalDate;

@Entity(value = "travelogues", useDiscriminator = false)
public class MongoDbTravelogue implements MongoDbEntity, Travelogue, ContextAwareEntity {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private LocalDate date;

    private int userId;

    private String title;

    private Integer titlePhotoId;

    private String summary;

    private String text;

    public MongoDbTravelogue() {
        // dummy for Morphia
    }

    @Transient
    private Context context;

    @Override
    public void inject(Context context) {
        this.context = context;
    }

    public MongoDbTravelogue(int id, String title, Integer titlePhotoId, LocalDate date, String summary, String text, int userId) {
        this.numId = id;
        this.userId = userId;
        this.date = date;
        this.title = title;
        this.titlePhotoId = titlePhotoId;
        this.summary = summary;
        this.text = text;
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public int getId() {
        return numId;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Transient
    private User user;

    @Override
    public User getUser() {
        if (user == null) {
            user = context.getUsersModel().get(userId);
        }
        return user;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Integer getTitlePhotoId() {
        return titlePhotoId;
    }

    public void setTitlePhotoId(Integer titlePhotoId) {
        this.titlePhotoId = titlePhotoId;
    }

    @Transient
    private Photo titlePhoto;

    @Override
    public Photo getTitlePhoto() {
        if (titlePhoto == null) {
            titlePhoto = context.getPhotosModel().get(titlePhotoId);
        }
        return titlePhoto;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toHtml(String lang) {
        return MarkupParser.parse(getText()).toHtml(lang, context.getPhotosModel());
    }

    @Override
    public String toBBCode(String lang) {
        return MarkupParser.parse(getText()).toBBCode(lang, context.getPhotosModel());
    }

    @Override
    public String toString() {
        return getId() + " [" + getTitle() + "]";
    }
}
