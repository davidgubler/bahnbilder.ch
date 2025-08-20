package models;

import entities.*;
import entities.aggregations.AggregationCountryViews;
import entities.formdata.PhotoFormData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface PhotosModel {
    void clear();

    Photo create(
            int id,
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
            int views);

    Photo create(
            Exif exif,
            int userId,
            String uploadFilename,
            Instant uploadDate,
            LocalDateTime photoDate,
            Integer licenseId,
            Integer photoTypeId,
            Integer countryId,
            Double longitude,
            Double latitude,
            Integer operatorId,
            Integer vehicleClassId,
            Integer nr);

    Photo get(Integer id);

    Stream<? extends Photo> getAll();

    Stream<? extends Photo> getByIds(Collection<Integer> ids);

    List<Photo> getFeatured(VehicleClassesModel vehicleClassesModel, VehicleTypesModel vehicleTypesModel);

    int getLocationCardinality(int locationId);

    int getVehicleClassCardinality(int vehicleClassId);

    List<? extends AggregationCountryViews> getTopCountryIdsByViews();

    Exif extractExif(byte[] data);

    void fetchExif(Photo photo);

    List<? extends Photo> search(Search search);

    Photo getNext(Photo photo, Search search);

    Photo getPrev(Photo photo, Search search);

    long searchCount(Search search);

    List<Integer> getLocationIdsByCountryId(Integer countryId);

    List<Integer> getOperatorIdsByCountryId(Integer countryId);

    List<Integer> getOperatorIdsByVehicleClassIds(Collection<Integer> vehicleClassIds);

    List<Integer> getVehicleClassIdsByCountryIdOperatorId(Integer countryId, Integer operatorId);

    List<Integer> getCountryIdsByOperatorAndVehicleClassIds(Integer operatorId, Collection<Integer> vehicleClassIds);

    List<Integer> getCountryIdsByOperatorId(Integer operatorId);

    List<Integer> getVehicleClassIdsByOperatorId(Integer operatorId);

    List<Integer> getNrsByOperatorAndVehicleClassId(Integer operatorId, Integer vehicleClassId);

    void rate(Photo photo, int authorRating);

    Stream<? extends Photo> getCoordinates(Search search);

    List<Integer> getUsedCountryIds();

    List<Integer> getUsedOperatorIds();

    List<Integer> getUsedVehicleClassIds();

    void update(PhotoFormData data, LocalDateTime dateTime, Integer locationId, Set<String> labelsToAdd, Set<String> labelsToRemove);

    void update(Photo photo, Operator operator, VehicleClass vehicleClass, Integer nr);

    void update(Photo photo, Exif exif);

    void updateLabelsTexts(Photo photo, List<String> labels, List<String> texts);

    void addViews(int photoId, int views);

    void delete(PhotoFormData data);

    List<Integer> getOperatorIdsByCountryAndCoordinates(Photo photo, double radiusKm);

    Map<Integer, Long> getOperatorMatchesByText(Photo photo, String text);

    long countPhotosByOperator(Photo photo, Integer operatorId);
}
