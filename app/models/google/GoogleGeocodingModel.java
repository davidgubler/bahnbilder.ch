package models.google;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import entities.Country;
import entities.Station;
import models.CountriesModel;
import models.GeocodingModel;
import utils.Config;
import utils.geometry.DistanceComparator;
import utils.geometry.SimplePoint;
import utils.geometry.Point;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoogleGeocodingModel implements GeocodingModel {
    private final HttpClient client;

    private static final ObjectMapper MAPPER;

    @Inject
    private CountriesModel countriesModel;

    static {
        MAPPER = new ObjectMapper();
        // configure mapper to use fields instead of setter/getter/constructor
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GoogleGeocodingModel() {
        client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    private <T> Function<HttpResponse<byte[]>, T> jsonResponseHandler(TypeReference<T> responseType) {
        return httpResponse -> {
            if (httpResponse.statusCode() == 404) {
                return null;
            }
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                throw new CompletionException("HTTP response " + httpResponse.statusCode(), null);
            }
            try {
                return httpResponse.body() == null ? null : MAPPER.readValue(httpResponse.body(), responseType);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        };
    }

    @Override
    public CompletableFuture<Country> getCountryByPoint(Point point)  {
        URI uri;
        try {
            uri = new URI("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + point.getLat() + "," + point.getLng() + "&result_type=country&key=" + Config.Option.GOOGLE_MAPS_SERVER_KEY.get());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpRequest req = HttpRequest.newBuilder(uri).build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(jsonResponseHandler(new TypeReference<GoogleGeocodeResponse>() {}))
                .thenApply(ggr -> {
                    if (ggr != null && ggr.results.size() > 0 && ggr.results.get(0).address_components.size() > 0) {
                        return countriesModel.getByCode(ggr.results.get(0).address_components.get(0).short_name);
                    } else {
                        return null;
                    }
                });
    }

    @Override
    public List<Station> getNearbyStations(Point point) {
        List<Integer> distances = List.of(2500, 5000, 10000, 25000, 50000);
        for (int distance : distances) {
            URI uri;
            try {
                uri = new URI("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + point.getLat() + "," + point.getLng() + "&types=train_station&radius=" + distance + "&language=de&key=" + Config.Option.GOOGLE_MAPS_SERVER_KEY.get());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            HttpRequest req = HttpRequest.newBuilder(uri).build();
            try {
                GoogleGeocodeResponse r = client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(new TypeReference<GoogleGeocodeResponse>() {})).get();
                if (r.results.size() >= 3) {
                    return r.results.stream().map(rr -> new Station(rr.name, new SimplePoint(rr.geometry.location.lat, rr.geometry.location.lng))).sorted(new DistanceComparator(point)).collect(Collectors.toUnmodifiableList());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    private static class GoogleGeocodeResonseResultAddressComponent {
        String short_name;
    }

    private static class GooglePoint {
        Double lat;
        Double lng;
    }

    private static class GoogleGeocodeResponseResultGeometry {
        GooglePoint location;
    }

    private static class GoogleGeocodeResonseResult {
        List<GoogleGeocodeResonseResultAddressComponent> address_components;
        GoogleGeocodeResponseResultGeometry geometry;
        String name;
    }

    private static class GoogleGeocodeResponse {
        List<GoogleGeocodeResonseResult> results;
    }
}
