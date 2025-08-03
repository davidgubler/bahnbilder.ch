package services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import utils.geometry.Point;
import play.inject.ApplicationLifecycle;
import utils.Config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;


public class Railinfo {

    private final HttpClient client;

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String hostname;

    @Inject
    public Railinfo(ApplicationLifecycle appLifecycle) {
        hostname = Config.Option.RAILINFO_HOSTNAME.get();
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

    public CompletableFuture<List<String>> guessTheTrain(String countryCode, Point point, LocalDate date, LocalTime time) {
        if (hostname == null) {
            throw new IllegalStateException("hostname not configured");
        }
        URI uri;
        try {
            uri = new URI("https://" + hostname + "/api/" + countryCode.toLowerCase() + "/guessthetrain?lat=" + point.getLat() + "&lng=" + point.getLng() + "&date=" + date + "&time=" + time);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpRequest req = HttpRequest.newBuilder(uri).build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(new TypeReference<>() {}));
    }
}
