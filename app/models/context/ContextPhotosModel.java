package models.context;

import com.google.inject.Inject;
import entities.*;
import entities.aggregations.AggregationCountryViews;
import entities.formdata.PhotoFormData;
import models.PhotosModel;
import models.VehicleClassesModel;
import models.VehicleTypesModel;
import utils.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ContextPhotosModel extends ContextModel implements PhotosModel {

    @Inject
    private PhotosModel photosModel;

    public ContextPhotosModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { photosModel.clear(); return null; });
    }

    @Override
    public Photo create(int id, int userId, String photographer, String uploadFilename, Instant uploadDate, LocalDateTime photoDate, Integer licenseId, Integer photoTypeId, Integer countryId, Integer locationId, Double longitude, Double latitude, Integer operatorId, Integer vehicleClassId, Integer nr, String description, List<String> texts, List<String> labels, int authorRating, int views) {
        return call(() -> photosModel.create(id, userId, photographer, uploadFilename, uploadDate, photoDate, licenseId, photoTypeId, countryId, locationId, longitude, latitude, operatorId, vehicleClassId, nr, description, texts, labels, authorRating, views));
    }

    @Override
    public Photo create(Exif exif, int userId, String uploadFilename, Instant uploadDate, LocalDateTime photoDate, Integer licenseId, Integer photoTypeId, Integer countryId, Double longitude, Double latitude, Integer operatorId, Integer vehicleClassId, Integer nr) {
        return call(() -> photosModel.create(exif, userId, uploadFilename, uploadDate, photoDate, licenseId, photoTypeId, countryId, longitude, latitude, operatorId, vehicleClassId, nr));
    }

    @Override
    public Photo get(Integer id) {
        return call(() -> photosModel.get(id));
    }

    @Override
    public Stream<? extends Photo> getAll() {
        return call(() -> photosModel.getAll());
    }

    @Override
    public Stream<? extends Photo> getByIds(Collection<Integer> ids) {
        return call(() -> photosModel.getByIds(ids));
    }

    @Override
    public List<Photo> getFeatured(VehicleClassesModel vehicleClassesModel, VehicleTypesModel vehicleTypesModel) {
        return call(() -> photosModel.getFeatured(vehicleClassesModel, vehicleTypesModel));
    }

    @Override
    public int getLocationCardinality(int locationId) {
        return call(() -> photosModel.getLocationCardinality(locationId));
    }

    @Override
    public int getVehicleClassCardinality(int vehicleClassId) {
        return call(() -> photosModel.getVehicleClassCardinality(vehicleClassId));
    }

    @Override
    public List<? extends AggregationCountryViews> getTopCountryIdsByViews() {
        return call(() -> photosModel.getTopCountryIdsByViews());
    }

    @Override
    public Exif extractExif(byte[] data) {
        return call(() -> photosModel.extractExif(data));
    }

    @Override
    public void fetchExif(Photo photo) {
        call(() -> { photosModel.fetchExif(photo); return null; });
    }

    @Override
    public List<? extends Photo> search(Search search) {
        return call(() -> photosModel.search(search));
    }

    @Override
    public Photo getNext(Photo photo, Search search) {
        return call(() -> photosModel.getNext(photo, search));
    }

    @Override
    public Photo getPrev(Photo photo, Search search) {
        return call(() -> photosModel.getPrev(photo, search));
    }

    @Override
    public long searchCount(Search search) {
        return call(() -> photosModel.searchCount(search));
    }

    @Override
    public List<Integer> getLocationIdsByCountryId(Integer countryId) {
        return call(() -> photosModel.getLocationIdsByCountryId(countryId));
    }

    @Override
    public List<Integer> getOperatorIdsByCountryId(Integer countryId) {
        return call(() -> photosModel.getOperatorIdsByCountryId(countryId));
    }

    @Override
    public List<Integer> getOperatorIdsByVehicleClassIds(Collection<Integer> vehicleClassIds) {
        return call(() -> photosModel.getOperatorIdsByVehicleClassIds(vehicleClassIds));
    }

    @Override
    public List<Integer> getVehicleClassIdsByCountryIdOperatorId(Integer countryId, Integer operatorId) {
        return call(() -> photosModel.getVehicleClassIdsByCountryIdOperatorId(countryId, operatorId));
    }

    @Override
    public List<Integer> getCountryIdsByOperatorAndVehicleClassIds(Integer operatorId, Collection<Integer> vehicleClassIds) {
        return call(() -> photosModel.getCountryIdsByOperatorAndVehicleClassIds(operatorId, vehicleClassIds));
    }

    @Override
    public List<Integer> getCountryIdsByOperatorId(Integer operatorId) {
        return call(() -> photosModel.getCountryIdsByOperatorId(operatorId));
    }

    @Override
    public List<Integer> getVehicleClassIdsByOperatorId(Integer operatorId) {
        return call(() -> photosModel.getVehicleClassIdsByOperatorId(operatorId));
    }

    @Override
    public List<Integer> getNrsByOperatorAndVehicleClassId(Integer operatorId, Integer vehicleClassId) {
        return call(() -> photosModel.getNrsByOperatorAndVehicleClassId(operatorId, vehicleClassId));
    }

    @Override
    public void rate(Photo photo, int authorRating) {
        call(() -> { photosModel.rate(photo, authorRating); return null; });
    }

    @Override
    public Stream<? extends Photo> getCoordinates(Search search) {
        return call(() -> photosModel.getCoordinates(search));
    }

    @Override
    public List<Integer> getUsedCountryIds() {
        return call(() -> photosModel.getUsedCountryIds());
    }

    @Override
    public List<Integer> getUsedOperatorIds() {
        return call(() -> photosModel.getUsedOperatorIds());
    }

    @Override
    public List<Integer> getUsedVehicleClassIds() {
        return call(() -> photosModel.getUsedVehicleClassIds());
    }

    @Override
    public void update(PhotoFormData data, LocalDateTime dateTime, Integer locationId, Set<String> labelsToAdd, Set<String> labelsToRemove) {
        call(() -> { photosModel.update(data, dateTime, locationId, labelsToAdd, labelsToRemove); return null; });
    }

    @Override
    public void update(Photo photo, Operator operator, VehicleClass vehicleClass, Integer nr) {
        call(() -> { photosModel.update(photo, operator, vehicleClass, nr); return null; });
    }

    @Override
    public void update(Photo photo, Exif exif) {
        call(() -> { photosModel.update(photo, exif); return null; });
    }

    @Override
    public void updateLabelsTexts(Photo photo, List<String> labels, List<String> texts) {
        call(() -> { photosModel.updateLabelsTexts(photo, labels, texts); return null; });
    }

    @Override
    public void addViews(int photoId, int views) {
        call(() -> { photosModel.addViews(photoId, views); return null; });
    }

    @Override
    public void delete(PhotoFormData data) {
        call(() -> { photosModel.delete(data); return null; });
    }

    @Override
    public List<Integer> getOperatorIdsByCountryAndCoordinates(Photo photo, double radiusKm) {
        return call(() -> photosModel.getOperatorIdsByCountryAndCoordinates(photo, radiusKm));
    }

    @Override
    public Map<Integer, Long> getOperatorMatchesByText(Photo photo, String text) {
        return call(() -> photosModel.getOperatorMatchesByText(photo, text));
    }

    @Override
    public long countPhotosByOperator(Photo photo, Integer operatorId) {
        return call(() -> photosModel.countPhotosByOperator(photo, operatorId));
    }

    @Override
    public List<String> getPhotographers() {
        return call(() -> photosModel.getPhotographers());
    }

    @Override
    public Integer getMostCommonVehicleClassByCountry(Country country) {
        return call(() -> photosModel.getMostCommonVehicleClassByCountry(country));
    }

    @Override
    public int getVehicleClassCountByCountry(Country country) {
        return call(() -> photosModel.getVehicleClassCountByCountry(country));
    }

    @Override
    public int getVehicleCountByCountry(Country country) {
        return call(() -> photosModel.getVehicleCountByCountry(country));
    }

    @Override
    public List<Integer> getLatestVehicleClassIdAdditionsByCountry(Country country) {
        return call(() -> photosModel.getLatestVehicleClassIdAdditionsByCountry(country));
    }
}
