package models.mongodb;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.google.inject.Inject;
import com.mongodb.WriteConcern;
import dev.morphia.DeleteOptions;
import dev.morphia.UpdateOptions;
import dev.morphia.aggregation.expressions.AccumulatorExpressions;
import dev.morphia.aggregation.expressions.DateExpressions;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Group;
import dev.morphia.aggregation.stages.Sort;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import dev.morphia.query.MorphiaCursor;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import entities.*;
import entities.aggregations.AggregationCountryViews;
import entities.comparators.PhotoInterestingComparator;
import entities.formdata.PhotoFormData;
import entities.mongodb.MongoDbPhotoExif;
import entities.mongodb.MongoDbPhoto;
import entities.mongodb.aggregations.MongoDbAggregationCountryViews;
import models.*;
import utils.geometry.GeographicCoordinates;
import utils.geometry.SimplePoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MongoDbPhotosModel extends MongoDbModel<MongoDbPhoto> implements PhotosModel {

    @Inject
    private LocationsModel locationsModel;

    @Inject
    private VehicleClassesModel vehicleClassesModel;

    @Inject
    private OperatorsModel operatorsModel;

    @Inject
    private KeywordsModel keywordsModel;

    @Inject
    private FilesOriginalModel filesOriginalModel;

    @Override
    public Photo create(int id,
                        int userId,
                        String photographer,
                        String uploadFilename,
                        Instant uploadDate,
                        LocalDateTime photoDate,
                        Integer licenseId,
                        Integer photoTypeId,
                        Integer countryId,
                        Integer locationId,
                        Double longitude,
                        Double latitude,
                        Integer operatorId,
                        Integer vehicleClassId,
                        Integer nr,
                        String description,
                        List<String> texts,
                        List<String> labels,
                        int authorRating,
                        int views) {
        Photo photo = new MongoDbPhoto(id, null, userId, photographer, uploadFilename, uploadDate, photoDate, licenseId, photoTypeId, countryId, locationId, longitude, latitude, operatorId, vehicleClassId, nr, description, texts, labels, authorRating, views);
        mongoDb.getDs().save(photo);
        inject(photo);
        return photo;
    }

    @Override
    public Photo create(Exif exif, int userId, String uploadFilename, Instant uploadDate, LocalDateTime photoDate, Integer licenseId, Integer photoTypeId, Integer countryId, Double longitude, Double latitude, Integer operatorId, Integer vehicleClassId, Integer nr) {
        Photo photo = null;
        for (int i = 0; i < 10; i++) {
            try {
                photo = new MongoDbPhoto(getNextNumId(), (MongoDbPhotoExif)exif, userId, null, uploadFilename, uploadDate, photoDate, licenseId, photoTypeId, countryId, null, longitude, latitude, operatorId, vehicleClassId, nr, null, null, null, 3, 0);
                mongoDb.getDs().save(photo);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        inject(photo);
        return photo;
    }

    @Override
    public List<Photo> getFeatured(VehicleClassesModel vehicleClassesModel, VehicleTypesModel vehicleTypesModel) {
        long start = System.currentTimeMillis();
        List<AggregationDate> lastAggregationDates = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("photoDate", null), Filters.ne("vehicleClassId", null), Filters.ne("locationId", null))
                .group(Group.group().field("_id", DateExpressions.dateToString().date("$photoDate").format("%Y-%m-%d")))
                .sort(Sort.sort().descending("_id"))
                .limit(5)
                .execute(AggregationDate.class).toList();

        List<LocalDate> lastDates = lastAggregationDates.stream().map(AggregationDate::toDate).toList();
        List<Photo> featured = new LinkedList<>();
        Comparator<Photo> photoComparator = new PhotoInterestingComparator(this, vehicleClassesModel, vehicleTypesModel);

        for (LocalDate date : lastDates) {
            List<MongoDbPhoto> candidates = new ArrayList(query()
                    .filter(Filters.gte("photoDate", date))
                    .filter(Filters.lt("photoDate", date.plusDays(1)))
                    .stream().toList());
            if (candidates.isEmpty()) {
                continue; // just to catch potential race conditions - shouldn't happen normally
            }
            Collections.sort(candidates, photoComparator);
            featured.add(inject(candidates.get(0)));
        }
        return featured;
    }

    @Override
    public int getLocationCardinality(int locationId) {
        return (int)query().filter(Filters.eq("locationId", locationId)).count();
    }

    @Override
    public int getVehicleClassCardinality(int vehicleClassId) {
        return (int)query().filter(Filters.eq("vehicleClassId", vehicleClassId)).count();
    }

    @Override
    public List<? extends AggregationCountryViews> getTopCountryIdsByViews() {
        List<MongoDbAggregationCountryViews> topCountryIdsByViews = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("countryId", null), Filters.ne("views", null))
                .group(Group.group().field("_id", Expressions.field("countryId")).field("views", AccumulatorExpressions.sum(Expressions.field("views"))))
                .sort(Sort.sort().descending("views"))
                .limit(15)
                .execute(MongoDbAggregationCountryViews.class).toList();

        return inject(topCountryIdsByViews);
    }

    private static final DateTimeFormatter EXIF_DATE_TIME = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Override
    public MongoDbPhotoExif extractExif(byte[] data) {
        MongoDbPhotoExif exif;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data));

            exif = new MongoDbPhotoExif();

            for (Directory directory : metadata.getDirectories()) {
                if (directory instanceof GpsDirectory) {
                    GeoLocation geoLocation = ((GpsDirectory)directory).getGeoLocation();
                    if (geoLocation != null) {
                        // Just because there's a GpsDirectory there aren't necessarily coordinates
                        exif.setLat(geoLocation.getLatitude());
                        exif.setLng(geoLocation.getLongitude());
                    }
                    continue;
                }

                if (directory instanceof JpegDirectory) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagType() == 1) {
                            try {
                                exif.setHeight(Integer.parseInt(tag.getDescription().split(" ")[0]));
                            } catch (Exception e) {
                                throw new IOException("Photo has unparseable data in JpegDirectory/1 (height): " + e.getMessage());
                            }
                        }
                        if (tag.getTagType() == 3) {
                            try {
                                exif.setWidth(Integer.parseInt(tag.getDescription().split(" ")[0]));
                            } catch (Exception e) {
                                throw new IOException("Photo has unparseable data in JpegDirectory/3 (width): " + e.getMessage());
                            }
                        }
                    }
                    continue;
                }

                if (directory instanceof ExifIFD0Directory) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagType() == 272) {
                            exif.setCamera(tag.getDescription());
                        }
                    }
                    continue;
                }

                if (directory instanceof ExifSubIFDDirectory) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagType() == 33434) {
                            exif.setExposure(tag.getDescription());
                        }
                        if (tag.getTagType() == 33437) {
                            exif.setAperture(tag.getDescription());
                        }
                        if (tag.getTagType() == 34855) {
                            exif.setSensitivity(tag.getDescription());
                        }
                        if (tag.getTagType() == 37386) {
                            exif.setFocalLength(tag.getDescription());
                        }
                        if (tag.getTagType() == 36867) {
                            try {
                                String description = tag.getDescription().replace(": ", ":0"); // some weird programs don't use leading zeroes in date formats
                                exif.setDateTime(LocalDateTime.parse(description, EXIF_DATE_TIME));
                            } catch (Exception e) {
                                // too bad, continue
                            }
                        }
                    }
                    continue;
                }

                if (directory instanceof IptcDirectory) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagType() == IptcDirectory.TAG_KEYWORDS) {
                            exif.setKeywords(tag.getDescription());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Photo is not parseable: " + e.getMessage());
        } catch (ImageProcessingException e) {
            throw new RuntimeException("Photo is not parseable: " + e.getMessage());
        }
        return exif;
    }

    @Override
    public void fetchExif(Photo photo) {
        MongoDbPhotoExif exif;
        File file = filesOriginalModel.get(photo);
        exif = extractExif(file.getData());
        ((MongoDbPhoto)photo).setExif(exif);
        query(photo).update(new UpdateOptions(), UpdateOperators.set("exif", exif));
    }

    @Override
    public List<? extends Photo> search(Search search) {
        FindOptions findOptions = getSortOptions(search.getSortBy(), false);

        //findOptions.projection().include("numId", "resolutions", "operatorId", "vehicleClassId", "locationId", "nr"); // not worth it
        findOptions.skip((search.getPage() - 1) * Search.RESULTS_PER_PAGE);
        findOptions.limit(Search.RESULTS_PER_PAGE);

        List<MongoDbPhoto> photos = query(search).stream(findOptions).toList();

        // prepopulate some data
        Map<Integer, ? extends Operator> operators = operatorsModel.getByIdsAsMap(photos.stream().map(Photo::getOperatorId).filter(Objects::nonNull).collect(Collectors.toSet()));
        Map<Integer, ? extends VehicleClass> vehicleClasses = vehicleClassesModel.getByIdsAsMap(photos.stream().map(Photo::getVehicleClassId).filter(Objects::nonNull).collect(Collectors.toSet()));
        Map<Integer, ? extends Location> locations = locationsModel.getByIdsAsMap(photos.stream().map(Photo::getLocationId).filter(Objects::nonNull).collect(Collectors.toSet()));
        photos.forEach(p -> {
            p.setOperator(operators.get(p.getOperatorId()));
            p.setVehicleClass(vehicleClasses.get(p.getVehicleClassId()));
            p.setLocation(locations.get(p.getLocationId()));
        });

        inject(photos); // may be required to determine resolutions, everything else should be prepopulated
        return photos;
    }

    private Filter getFilter(String field, Object value, boolean forward) {
        return forward ? Filters.gt(field, value) : Filters.lt(field, value);
    }

    private dev.morphia.query.Sort getSort(String field, boolean forward) {
        return forward ? dev.morphia.query.Sort.ascending(field) : dev.morphia.query.Sort.descending(field);
    }

    private FindOptions getSortOptions(Search.SortBy sortBy, boolean forward) {
        FindOptions findOptions = new FindOptions();
        if (sortBy == Search.SortBy.views) {
            findOptions.sort(getSort("views", forward), getSort("numId", forward));
        } else if (sortBy == Search.SortBy.uploadDate) {
            findOptions.sort(getSort("uploadDate", forward), getSort("numId", forward));
        } else if (sortBy == Search.SortBy.rating) {
            findOptions.sort(getSort("authorRating", forward), getSort("views", forward), getSort("numId", forward));
        } else if (sortBy == Search.SortBy.photoDate) {
            findOptions.sort(getSort("photoDate", forward), getSort("numId", forward));
        } else if (sortBy == Search.SortBy.photoType) {
            findOptions.sort(getSort("photoTypeId", !forward), getSort("authorRating", forward), getSort("photoDate", forward), getSort("numId", forward));
        }
        findOptions = findOptions.allowDiskUse(true); // shouldn't be necessary as we have indexes but who knows.
        return findOptions;
    }

    public Photo getPrev(Photo photo, Search search, boolean forward) {
        Query<MongoDbPhoto> query = query(search);
        if (search.getSortBy() == Search.SortBy.views) {
            query = query.filter(Filters.or(
                    getFilter("views", photo.getViews(), forward),
                    Filters.and(
                            Filters.eq("views", photo.getViews()),
                            getFilter("numId", photo.getId(), forward)
                    )));
        } else if (search.getSortBy() == Search.SortBy.uploadDate) {
            query = query.filter(Filters.or(
                    getFilter("uploadDate", photo.getUploadDate(), forward),
                    Filters.and(
                            Filters.eq("uploadDate", photo.getUploadDate()),
                            getFilter("numId", photo.getId(), forward)
                    )));
        } else if (search.getSortBy() == Search.SortBy.rating) {
            query = query.filter(Filters.or(
                    getFilter("authorRating", photo.getAuthorRating(), forward),
                    Filters.and(
                            Filters.eq("authorRating", photo.getAuthorRating()),
                            getFilter("views", photo.getViews(), forward)
                    ),
                    Filters.and(
                            Filters.eq("authorRating", photo.getAuthorRating()),
                            Filters.eq("views", photo.getViews()),
                            getFilter("numId", photo.getId(), forward)
                    )));
        } else {
            query = query.filter(Filters.or(
                    getFilter("photoDate", photo.getPhotoDate(), forward),
                    Filters.and(
                            Filters.eq("photoDate", photo.getPhotoDate()),
                            getFilter("numId", photo.getId(), forward)
                    )));
        }

        FindOptions findOptions = getSortOptions(search.getSortBy(), forward);
        findOptions.limit(1);
        return query.stream(findOptions).findFirst().orElse(null);
    }

    @Override
    public Photo getPrev(Photo photo, Search search) {
        return getPrev(photo, search, false);
    }

    @Override
    public Photo getNext(Photo photo, Search search) {
        return getPrev(photo, search, true);
    }

    @Override
    public long searchCount(Search search) {
        return query(search).count();
    }

    @Override
    public List<Integer> getLocationIdsByCountryId(Integer countryId) {
        if (countryId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
            .match(Filters.ne("locationId", null), Filters.eq("countryId", countryId))
            .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("locationId"))))
            .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getOperatorIdsByCountryId(Integer countryId) {
        if (countryId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("operatorId", null), Filters.eq("countryId", countryId))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("operatorId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getOperatorIdsByVehicleClassIds(Collection<Integer> vehicleClassIds) {
        if (vehicleClassIds == null || vehicleClassIds.isEmpty()) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("operatorId", null), Filters.in("vehicleClassId", vehicleClassIds))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("operatorId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getVehicleClassIdsByCountryIdOperatorId(Integer countryId, Integer operatorId) {
        if (countryId == null || operatorId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("vehicleClassId", null), Filters.eq("countryId", countryId), Filters.eq("operatorId", operatorId))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("vehicleClassId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getCountryIdsByOperatorAndVehicleClassIds(Integer operatorId, Collection<Integer> vehicleClassIds) {
        if (operatorId == null || vehicleClassIds == null || vehicleClassIds.isEmpty()) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("countryId", null), Filters.eq("operatorId", operatorId), Filters.in("vehicleClassId", vehicleClassIds))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("countryId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getCountryIdsByOperatorId(Integer operatorId) {
        if (operatorId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("countryId", null), Filters.eq("operatorId", operatorId))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("countryId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getVehicleClassIdsByOperatorId(Integer operatorId) {
        if (operatorId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("vehicleClassId", null), Filters.eq("operatorId", operatorId))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("vehicleClassId"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct : Collections.emptyList();
    }

    @Override
    public List<Integer> getNrsByOperatorAndVehicleClassId(Integer operatorId, Integer vehicleClassId) {
        if (operatorId == null || vehicleClassId == null) {
            return Collections.emptyList();
        }
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("nr", null), Filters.eq("operatorId", operatorId), Filters.eq("vehicleClassId", vehicleClassId))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("nr"))))
                .execute(AggregationDistinct.class);
        List<Integer> nrs = new ArrayList<>(cursor.hasNext() ? cursor.next().distinct : Collections.emptyList());
        Collections.sort(nrs);
        if (query().filter(Filters.eq("operatorId", operatorId), Filters.eq("vehicleClassId", vehicleClassId), Filters.eq("nr", null)).first() != null) {
            nrs.add(null);
        }
        return nrs;
    }

    private Query<MongoDbPhoto> queryIncompleteSearch(Search search) {
        Query<MongoDbPhoto> q = query();
        q = q.filter(Filters.eq("userId", search.getAuthorId()));
        q = q.filter(
            Filters.or(
                Filters.eq("licenseId", null),
                Filters.eq("countryId", null),
                Filters.eq("locationId", null),
                Filters.eq("photoTypeId", null),
                Filters.and(
                    Filters.in("photoTypeId", Set.of(1, 2, 3)),
                    Filters.or(
                        Filters.eq("operatorId", null),
                        Filters.eq("vehicleClassId", null)
                    )
                )
            )
        );
        return q;
    }

    private Query<MongoDbPhoto> query(Search search) {
        if (search instanceof IncompleteSearch) {
            return queryIncompleteSearch(search);
        }

        Query<MongoDbPhoto> q = query();

        if (search.getAuthorId() != null) {
            q = q.filter(Filters.eq("userId", search.getAuthorId()));
        }
        if (search.getPhotographer() != null) {
            q = q.filter(Filters.eq("photographer", search.getPhotographer()));
        }
        if (search.getLicenseId() != null) {
            q = q.filter(Filters.eq("licenseId", search.getLicenseId()));
        }
        if (search.getDateFrom() != null) {
            q = q.filter(Filters.gte("photoDate", search.getDateFrom()));
        }
        if (search.getDateTo() != null) {
            LocalDate nextDay = search.getDateTo().plusDays(1);
            q = q.filter(Filters.lt("photoDate", nextDay));
        }
        if (search.getPhotoTypeId() != null) {
            q = q.filter(Filters.eq("photoTypeId", search.getPhotoTypeId()));
        }
        if (search.getCountryId() != null) {
            q = q.filter(Filters.eq("countryId", search.getCountryId()));
        }
        if (search.getLocationId() != null) {
            Filter filter = Filters.eq("locationId", search.getLocationId());
            Location reverseLocation = locationsModel.getReverseLocation(search.getLocationId());
            if (reverseLocation != null) {
                filter = Filters.or(filter, Filters.eq("locationId", reverseLocation.getId()));
            }
            q = q.filter(filter);
        }
        if (search.getOperatorId() != null) {
            q = q.filter(Filters.eq("operatorId", search.getOperatorId()));
        }
        if (search.getVehicleClassId() != null) {
            if (search.getIncludeVehicleSeries()) {
                VehicleClass vehicleClass = vehicleClassesModel.get(search.getVehicleClassId());
                Integer vehicleSeriesId = vehicleClass == null ? null : vehicleClass.getVehicleSeriesId();
                List<Integer> vehicleClassIds = vehicleClassesModel.getByVehicleSeriesId(vehicleSeriesId).map(VehicleClass::getId).collect(Collectors.toUnmodifiableList());
                q = q.filter(Filters.in("vehicleClassId", vehicleClassIds));
            } else {
                q = q.filter(Filters.eq("vehicleClassId", search.getVehicleClassId()));
            }
        }
        if (search.getNr() != null) {
            if (search.getNr() >= 0) {
                q = q.filter(Filters.eq("nr", search.getNr()));
            } else {
                q = q.filter(Filters.eq("nr", null));
            }
        }
        if (search.getDescription() != null) {
            q = q.filter(Filters.text(search.getDescription()));
        }
        if (search.getKeywords() != null) {
            List<Filter> keywordsFilters = new ArrayList<>();
            for (String keywordStr : search.getKeywords()) {
                boolean not = keywordStr.startsWith("-");
                keywordStr = not ? keywordStr.substring(1) : keywordStr;

                Keyword keyword = keywordsModel.getByName(keywordStr);
                if (keyword == null) {
                    continue;
                }
                keywordsFilters.add(not ? Filters.nin("labels", keyword.getLabels()) : Filters.in("labels", keyword.getLabels()));
            }
            if (!keywordsFilters.isEmpty()) {
                q = q.filter(Filters.and(keywordsFilters.toArray(new Filter[0])));
            }
        }
        if (search.getCoordinates() != null) {
            // FIXME this should be replaced by a proper geospatial search: https://www.mongodb.com/docs/manual/geospatial-queries/
            // But for that we need to change the data format in the DB which is not fun.
            SimplePoint north = GeographicCoordinates.goNorth(search.getCoordinates(), search.getRadiusKm());
            SimplePoint south = GeographicCoordinates.goNorth(search.getCoordinates(), -search.getRadiusKm());
            SimplePoint east = GeographicCoordinates.goEast(search.getCoordinates(), search.getRadiusKm());
            SimplePoint west = GeographicCoordinates.goEast(search.getCoordinates(), -search.getRadiusKm());
            q = q.filter(Filters.lt("lat", north.getLat()));
            q = q.filter(Filters.gt("lat", south.getLat()));
            q = q.filter(Filters.lt("lng", east.getLng()));
            q = q.filter(Filters.gt("lng", west.getLng()));
        }
        return q;
    }

    @Override
    public void rate(Photo photo, int authorRating) {
        if (photo.getAuthorRating() != authorRating) {
            ((MongoDbPhoto)photo).setAuthorRating(authorRating);
            query(photo).update(new UpdateOptions(), UpdateOperators.set("authorRating", authorRating));
        }
    }

    @Override
    public Stream<? extends Photo> getCoordinates(Search search) {
        return query(search)
                .filter(Filters.ne("lat", null), Filters.ne("lng", null))
                .stream(new FindOptions().sort(dev.morphia.query.Sort.descending("photoDate")).projection().include("numId", "lat", "lng"));
    }

    @Override
    public List<Integer> getUsedCountryIds() {
        return mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("countryId"))))
                .execute(AggregationDistinct.class).next().distinct;
    }

    @Override
    public List<Integer> getUsedOperatorIds() {
        return mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("operatorId"))))
                .execute(AggregationDistinct.class).next().distinct;
    }

    @Override
    public List<Integer> getUsedVehicleClassIds() {
        return mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("vehicleClassId"))))
                .execute(AggregationDistinct.class).next().distinct;
    }

    @Override
    public void update(PhotoFormData data, LocalDateTime dateTime, Integer locationId, Set<String> labelsToAdd, Set<String> labelsToRemove) {
        List<Integer> photoIds = data.photos.stream().map(Photo::getId).collect(Collectors.toUnmodifiableList());
        Query<MongoDbPhoto> q = query().filter(Filters.in("numId", photoIds));

        List<UpdateOperator> ops = new ArrayList<>();
        if (photoIds.size() == 1 && dateTime == null) {
            ops.add(UpdateOperators.unset("photoDate"));
        }
        if (dateTime != null) {
            ops.add(UpdateOperators.set("photoDate", dateTime));
        }

        if (photoIds.size() == 1 && locationId == null) {
            ops.add(UpdateOperators.unset("locationId"));
        }
        if (locationId != null) {
            ops.add(UpdateOperators.set("locationId", locationId));
        }

        if (!labelsToRemove.isEmpty()) {
            ops.add(UpdateOperators.pullAll("labels", new ArrayList<>(labelsToRemove)));
        }

        if (photoIds.size() == 1 && data.photographer == null) {
            ops.add(UpdateOperators.unset("photographer"));
        }
        if (data.photographer != null) {
            ops.add(UpdateOperators.set("photographer", data.photographer));
        }

        if (photoIds.size() == 1 && data.licenseId == null) {
            ops.add(UpdateOperators.unset("licenseId"));
        }
        if (data.licenseId != null) {
            ops.add(UpdateOperators.set("licenseId", data.licenseId));
        }

        if (photoIds.size() == 1 && data.photoTypeId == null) {
            ops.add(UpdateOperators.unset("photoTypeId"));
        }
        if (data.photoTypeId != null) {
            ops.add(UpdateOperators.set("photoTypeId", data.photoTypeId));
        }

        if (photoIds.size() == 1 && data.countryId == null) {
            ops.add(UpdateOperators.unset("countryId"));
        }
        if (data.countryId != null) {
            ops.add(UpdateOperators.set("countryId", data.countryId));
        }

        if (photoIds.size() == 1 && data.operatorId == null) {
            ops.add(UpdateOperators.unset("operatorId"));
        }
        if (data.operatorId != null) {
            ops.add(UpdateOperators.set("operatorId", data.operatorId));
        }

        if (photoIds.size() == 1 && data.vehicleClassId == null) {
            ops.add(UpdateOperators.unset("vehicleClassId"));
        }
        if (data.vehicleClassId != null) {
            ops.add(UpdateOperators.set("vehicleClassId", data.vehicleClassId));
        }

        if (photoIds.size() == 1 && data.nr == null) {
            ops.add(UpdateOperators.unset("nr"));
        }
        if (data.nr != null) {
            ops.add(UpdateOperators.set("nr", data.nr));
        }

        if (photoIds.size() == 1 && data.description == null) {
            ops.add(UpdateOperators.unset("description"));
        }
        if (data.description != null) {
            ops.add(UpdateOperators.set("description", data.description));
        }

        if (data.lat != null && data.lng != null) {
            ops.add(UpdateOperators.set("lat", data.lat));
            ops.add(UpdateOperators.set("lng", data.lng));
        }
        if (!ops.isEmpty()) {
            q.update(new UpdateOptions().multi(true), ops.toArray(new UpdateOperator[ops.size()]));
        }

        
        if (!labelsToAdd.isEmpty()) {
            // can't do a pull and push operation on "labels" in the same query
            query().filter(Filters.in("numId", photoIds)).update(new UpdateOptions().multi(true), UpdateOperators.addToSet("labels", labelsToAdd));
        }
    }

    @Override
    public void update(Photo photo, Operator operator, VehicleClass vehicleClass, Integer nr) {
        Query<MongoDbPhoto> q = query().filter(Filters.eq("numId", photo.getId()));
        List<UpdateOperator> ops = new ArrayList<>();
        ops.add(UpdateOperators.set("operatorId", operator.getId()));
        ops.add(UpdateOperators.set("vehicleClassId", vehicleClass.getId()));
        ops.add(UpdateOperators.set("nr", nr));
        q.update(new UpdateOptions(), ops.toArray(new UpdateOperator[ops.size()]));
    }

    @Override
    public void update(Photo photo, Exif exif) {
        MongoDbPhoto mongoDbPhoto = (MongoDbPhoto)photo;
        mongoDbPhoto.setExif(exif);
        query(photo).update(new UpdateOptions(), UpdateOperators.set("exif", mongoDbPhoto.getExif()));
    }

    @Override
    public void updateLabelsTexts(Photo photo, List<String> labels, List<String> texts) {
        MongoDbPhoto mongoDbPhoto = (MongoDbPhoto)photo;
        mongoDbPhoto.setLabels(labels);
        mongoDbPhoto.setTexts(texts);
        query(photo).update(new UpdateOptions(), UpdateOperators.set("labels", mongoDbPhoto.getLabels()), UpdateOperators.set("texts", mongoDbPhoto.getTexts()));
    }

    @Override
    public void addViews(int photoId, int views) {
        query().filter(Filters.eq("numId", photoId)).update(new UpdateOptions().writeConcern(WriteConcern.UNACKNOWLEDGED), UpdateOperators.inc("views", views));
    }

    @Override
    public void delete(PhotoFormData data) {
        Query<MongoDbPhoto> q = query().filter(Filters.in("numId", data.photos.stream().map(Photo::getId).collect(Collectors.toUnmodifiableList())));
        q.delete(new DeleteOptions().multi(true));
    }

    @Override
    public List<Integer> getOperatorIdsByCountryAndCoordinates(Photo photo, double radiusKm) {
        if (photo == null || (photo.getCountryId() == null && photo.getCoordinates() == null)) {
            return Collections.emptyList();
        }
        Search search = new Search(photo.getCountryId(), photo.getCoordinates(), radiusKm);
        return query(search)
                .filter(Filters.ne("numId", photo.getId()))
                .filter(Filters.ne("operatorId", null))
                .stream(new FindOptions().projection().include("operatorId"))
                .map(p -> p.getOperatorId()).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Map<Integer, Long> getOperatorMatchesByText(Photo photo, String text) {
        if (photo == null || photo.getCountryId() == null) {
            return Collections.emptyMap();
        }

        /*for (Photo p : query()
                .filter(Filters.ne("numId", photo.getId()))
                .filter(Filters.ne("operatorId", null))
                .filter(Filters.eq("countryId", photo.getCountryId()))
                .filter(Filters.eq("texts", text))
                .stream(new FindOptions()).toList() ) {
            Operator o = operatorsModel.get(p.getOperatorId());
            System.out.println("photo " + p.getId() + " with operator " + o + " matches text " + text);
        }*/

        return query()
                .filter(Filters.ne("numId", photo.getId()))
                .filter(Filters.ne("operatorId", null))
                .filter(Filters.eq("countryId", photo.getCountryId()))
                .filter(Filters.eq("texts", text))
                .stream(new FindOptions().projection().include("operatorId"))
                .map(p -> p.getOperatorId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public long countPhotosByOperator(Photo photo, Integer operatorId) {
        if (photo == null || photo.getCountryId() == null) {
            return 0;
        }
        return query()
                .filter(Filters.ne("numId", photo.getId()))
                .filter(Filters.eq("countryId", photo.getCountryId()))
                .filter(Filters.eq("operatorId", operatorId))
                .count();
    }

    @Override
    public List<String> getPhotographers() {
        MorphiaCursor<AggregationDistinct> cursor = mongoDb.getDs().aggregate(MongoDbPhoto.class)
                .match(Filters.ne("photographer", null))
                .group(Group.group().field("_id", null).field("distinct", AccumulatorExpressions.addToSet(Expressions.field("photographer"))))
                .execute(AggregationDistinct.class);
        return cursor.hasNext() ? cursor.next().distinct.stream().sorted().toList() : Collections.emptyList();
    }

    @Entity
    private static class AggregationDate {
        @Id
        private String _id;

        @Override
        public String toString() {
            return _id;
        }

        public LocalDate toDate() {
            return LocalDate.parse(_id);
        }
    }

    @Entity
    protected static class AggregationDistinct<T> {
        @Id
        private Object _id;

        List<T> distinct;
    }
}
