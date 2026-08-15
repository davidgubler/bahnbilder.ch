package models.cloudflare;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.DnsRecord;
import entities.cloudflare.CloudflareApiResult;
import entities.cloudflare.CloudflareDnsRecord;
import models.DnsModel;
import play.mvc.Http;
import utils.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

public class CloudflareDnsModel implements DnsModel {
    private final HttpClient client;

    private final String apiToken;

    private static final ObjectMapper MAPPER;

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

    public CloudflareDnsModel() {
        apiToken = Config.Option.CLOUDFLARE_API_TOKEN.get();
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

    private <T> CompletableFuture<T> get(URI uri, TypeReference<T> typeReference) {
        HttpRequest req = HttpRequest.newBuilder(uri).header(Http.HeaderNames.AUTHORIZATION, "Bearer " + apiToken).build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(typeReference));
    }

    private <T> CompletableFuture<T> delete(URI uri, TypeReference<T> typeReference) {
        HttpRequest req = HttpRequest.newBuilder(uri).header(Http.HeaderNames.AUTHORIZATION, "Bearer " + apiToken).DELETE().build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(typeReference));
    }

    private <T> CompletableFuture<T> post(URI uri, Object data, TypeReference<T> typeReference) throws JsonProcessingException  {
        HttpRequest req = HttpRequest.newBuilder(uri)
                .header(Http.HeaderNames.AUTHORIZATION, "Bearer " + apiToken)
                .header(Http.HeaderNames.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(data)))
                .build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(typeReference));
    }

    @Override
    public List<? extends DnsRecord> getRecords(String zoneId){
        try {
            URI uri = new URI("https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records");
            return get(uri, new TypeReference<CloudflareApiResult<List<CloudflareDnsRecord>>>(){}).get().getResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteRecord(String zoneId, DnsRecord record) {
        try {
            URI uri = new URI("https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records/" + record.getId());
            delete(uri, new TypeReference<CloudflareApiResult<CloudflareDnsRecord>>(){}).get();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public DnsRecord createRecord(String zoneId, String name, Integer ttl, String type, String content, Boolean proxied) {
        CloudflareDnsRecord record = new CloudflareDnsRecord(name, ttl, type, content, proxied);
        try {
            URI uri = new URI("https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records");
            return post(uri, record, new TypeReference<CloudflareApiResult<CloudflareDnsRecord>>(){}).get().getResult();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean check(URI uri) {
        HttpRequest req = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(15)).build();
        try {
            client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
