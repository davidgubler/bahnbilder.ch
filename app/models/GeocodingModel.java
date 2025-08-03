package models;

import entities.Country;
import entities.Station;
import utils.geometry.Point;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GeocodingModel {
    CompletableFuture<Country> getCountryByPoint(Point point);

    List<Station> getNearbyStations(Point point);
}
