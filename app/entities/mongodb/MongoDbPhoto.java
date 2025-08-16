package entities.mongodb;

import dev.morphia.annotations.*;
import entities.*;
import org.bson.types.ObjectId;
import play.libs.F;
import utils.Context;
import utils.geometry.SimplePoint;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.morphia.utils.IndexType.TEXT;

@Indexes({
        @Index(fields = @Field(value = "description", type = TEXT)),
        @Index(fields = { @Field(value = "views"), @Field(value = "numId") }, options = @IndexOptions(name = "sort_views_numId")),
        @Index(fields = { @Field(value = "uploadDate"), @Field(value = "numId") }, options = @IndexOptions(name = "sort_uploadDate_numId")),
        @Index(fields = { @Field(value = "authorRating"), @Field(value = "views"), @Field(value = "numId") }, options = @IndexOptions(name = "sort_authorRating_views_numId")),
        @Index(fields = { @Field(value = "photoDate"), @Field(value = "numId") }, options = @IndexOptions(name = "sort_photoDate_numId")),
})
@Entity(value = "photos", useDiscriminator = false)
public class MongoDbPhoto implements MongoDbEntity, Photo, ContextAwareEntity {
    @Id
    private ObjectId _id;

    @Indexed(options = @IndexOptions(unique = true))
    private int numId;

    private int userId;

    @Indexed
    private String photographer;

    @Indexed
    private Integer licenseId;

    private String uploadFilename;

    @Indexed
    private Instant uploadDate;

    @Indexed
    private LocalDateTime photoDate;

    @Indexed
    private Integer photoTypeId;

    @Indexed
    private Integer countryId;

    @Indexed
    private Integer locationId;

    @Indexed
    private Double lng;

    @Indexed
    private Double lat;

    @Indexed
    private Integer operatorId;

    @Indexed
    private Integer vehicleClassId;

    @Indexed
    private Integer nr;

    private String description;

    @Indexed
    private List<String> texts = Collections.emptyList();

    @Indexed
    private List<String> labels = Collections.emptyList();

    private int authorRating = 3;

    private int views = 0;

    private MongoDbPhotoExif exif = null;

    @Transient
    private Context context;

    @Override
    public void inject(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.context = context;
    }

    public MongoDbPhoto() {
        // dummy for morphia
    }

    public MongoDbPhoto(
            int id,
            MongoDbPhotoExif exif,
            int userId,
            String photographer,
            String uploadFilename,
            Instant uploadDate,
            LocalDateTime photoDate,
            Integer licenseId,
            Integer photoTypeId,
            Integer countryId,
            Integer locationId,
            Double lng,
            Double lat,
            Integer operatorId,
            Integer vehicleClassId,
            Integer nr,
            String description,
            List<String> texts,
            List<String> labels,
            Integer authorRating,
            int views) {
        this.numId = id;
        this.exif = exif;
        this.userId = userId;
        this.photographer = photographer;
        this.uploadFilename = uploadFilename;
        this.uploadDate = uploadDate;
        this.photoDate = photoDate;
        this.licenseId = (licenseId != null && licenseId == 0) ? null : licenseId;
        this.photoTypeId = (photoTypeId != null && photoTypeId == 0) ? null : photoTypeId;
        this.countryId = (countryId != null && countryId == 0) ? null : countryId;
        this.locationId = (locationId != null && locationId == 0) ? null : locationId;
        this.lng = lng;
        this.lat = lat;
        this.operatorId = (operatorId != null && operatorId == 0) ? null : operatorId;
        this.vehicleClassId = (vehicleClassId != null && vehicleClassId == 0) ? null : vehicleClassId;
        this.nr = nr;
        this.description = description;
        this.texts = (texts == null || texts.isEmpty()) ? null : texts;
        this.labels = (labels == null || labels.isEmpty()) ? null : labels;
        this.authorRating = authorRating;
        this.views = views;
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
    public String getUploadFilename() {
        return uploadFilename;
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
    public String getPhotographer() {
        return photographer;
    }

    @Override
    public LocalDateTime getPhotoDate() {
        //return photoDate == null ? null : LocalDateTime.parse(photoDate);
        return photoDate;
    }

    @Override
    public Instant getUploadDate() {
        //return uploadDate == null ? null : LocalDateTime.parse(uploadDate);
        return uploadDate;
    }

    @Override
    public Integer getPhotoTypeId() {
        return photoTypeId;
    }

    @Transient
    private PhotoType photoType = null;

    @Override
    public PhotoType getPhotoType() {
        if (photoType == null) {
            photoType = context.getPhotoTypesModel().get(photoTypeId);
        }
        return photoType;
    }

    @Override
    public Integer getLocationId() {
        return locationId;
    }

    @Transient
    private Location location = null;

    @Override
    public Location getLocation() {
        if (locationId != null && location == null){
            location = context.getLocationsModel().get(locationId);
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.locationId = location == null ? null : location.getId();
    }

    @Override
    public Integer getCountryId() {
        return countryId;
    }

    @Transient
    private Country country = null;

    @Override
    public Country getCountry() {
        if (country == null) {
            country = context.getCountriesModel().get(countryId);
        }
        return country;
    }

    @Override
    public Integer getOperatorId() {
        return operatorId;
    }

    @Transient
    private Operator operator = null;

    @Override
    public Operator getOperator() {
        if (operatorId != null && operator == null) {
            operator = context.getOperatorsModel().get(operatorId);
        }
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
        this.operatorId = operator != null ? operator.getId() : null;
    }

    @Override
    public Integer getVehicleClassId() {
        return vehicleClassId;
    }

    @Transient
    private VehicleClass vehicleClass = null;

    @Override
    public VehicleClass getVehicleClass() {
        if (vehicleClassId != null && vehicleClass == null) {
            vehicleClass = context.getVehicleClassesModel().get(vehicleClassId);
        }
        return vehicleClass;
    }

    public void setVehicleClass(VehicleClass vehicleClass) {
        this.vehicleClass = vehicleClass;
        this.vehicleClassId = vehicleClass != null ? vehicleClass.getId() : null;
    }

    @Override
    public Integer getNr() {
        return nr;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getLicenseId() {
        return licenseId;
    }

    @Transient
    private License license = null;

    @Override
    public License getLicense() {
        if (license == null) {
            license = context.getLicensesModel().get(licenseId);
        }
        return license;
    }

    @Override
    public int getViews() {
        return views;
    }

    private Integer uncollectedViews;

    @Override
    public int getViewsUncollected() {
        if (uncollectedViews == null) {
            uncollectedViews = context.getViewsModel().getUncollected(numId);
        }
        return views + uncollectedViews;
    }

    @Override
    public int getAuthorRating() {
        return authorRating;
    }

    public void setAuthorRating(int authorRating) {
        this.authorRating = authorRating;
    }

    @Override
    public Double getLng() {
        return lng;
    }

    @Override
    public Double getLat() {
        return lat;
    }

    @Override
    public SimplePoint getCoordinates() {
        if (lng == null || lat == null) {
            return null;
        }
        return new SimplePoint.PointBuilder().withLng(lng).withLat(lat).build();
    }

    @Override
    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    @Override
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setExif(Exif exif) {
        this.exif = (MongoDbPhotoExif)exif;
    }

    @Override
    public Exif getExif() {
        if (exif == null) {
            context.getPhotosModel().fetchExif(this);
        }
        return exif == null ? new MongoDbPhotoExif() : exif;
    }

    @Override
    public List<? extends PhotoResolution> getResolutions() {
        List<PhotoResolution> resolutions = new ArrayList<>();
        for (PhotoResolution.Size size : PhotoResolution.Size.values()) {
            if (size != PhotoResolution.Size.original) {
                F.Tuple<Integer, Integer> scaledSize = size.getScaledSize(getExif().getWidth(), getExif().getHeight());
                int scaledWidth = scaledSize._1;
                int scaledHeigh = scaledSize._2;
                if (scaledWidth != getExif().getWidth() && scaledHeigh != getExif().getHeight()) {
                    resolutions.add(new PhotoResolution(size, scaledWidth, scaledHeigh));
                }
            } else {
                resolutions.add(new PhotoResolution(size, getExif().getWidth(), getExif().getHeight()));
            }
        }
        return resolutions;
    }

    @Override
    public String getUrl(PhotoResolution resolution) {
        if (PhotoResolution.Size.original == resolution.getSize()) {
            return context.getFilesOriginalModel().getPublic(this);
        }
        return context.getFilesScaledModel().getPublic(this, resolution.getSize());
    }

    @Override
    public String getUrlOriginal() {
        return context.getFilesOriginalModel().getPublic(this);
    }

    @Override
    public String getUrlPublic200() {
        return context.getFilesScaledModel().getPublic(this, PhotoResolution.Size.small);
    }

    @Override
    public String getUrlPublic900() {
        return context.getFilesScaledModel().getPublic(this, PhotoResolution.Size.medium);
    }

    @Override
    public String getUrlPublic1280() {
        return context.getFilesScaledModel().getPublic(this, PhotoResolution.Size.large);
    }

    @Override
    public String getUrlPublic1600() {
        return context.getFilesScaledModel().getPublic(this, PhotoResolution.Size.xlarge);
    }

    @Override
    public String getUrlPublic2400() {
        return context.getFilesScaledModel().getPublic(this, PhotoResolution.Size.xxlarge);
    }

    @Override
    public String toString() {
        return "" + numId;
    }

    /**
     * Sort photos by photoDate and ID if necessary. Normally you shouldn't use this because if you use the Search object the photos will come pre-sorted from the DB.
     * This sorts the photos by date in descending order (latest first). It's useful for photo batch operations such
     * as edit or delete.
     * @param photo
     * @return
     */
    @Override
    public int compareTo(Photo photo) {
        if (photoDate != null && photo.getPhotoDate() != null && !photoDate.equals(photo.getPhotoDate())) {
            return -photoDate.compareTo(photo.getPhotoDate());
        }
        return -Integer.compare(this.numId, photo.getId());
    }
}
