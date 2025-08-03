package models.context;

import com.google.inject.Inject;
import entities.Country;
import entities.Station;
import models.GeocodingModel;
import utils.Context;
import utils.geometry.Point;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ContextGeocodingModel extends ContextModel implements GeocodingModel {

    @Inject
    private GeocodingModel geocodingModel;

    public ContextGeocodingModel(Context context) {
        this.context = context;
    }

    @Override
    public CompletableFuture<Country> getCountryByPoint(Point point) {
        // FIXME that doesn't make sense (it works though)
        return call(() -> geocodingModel.getCountryByPoint(point));
    }

    @Override
    public List<Station> getNearbyStations(Point point) {
        return call(() -> geocodingModel.getNearbyStations(point));
    }
}
