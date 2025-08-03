package models.google;

import com.adobe.internal.xmp.impl.Base64;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import entities.File;
import entities.Photo;
import entities.PhotoResolution;
import models.FilesOriginalModel;
import models.FilesScaledModel;
import models.VisionModel;
import play.libs.F;
import play.mvc.Http;
import utils.Config;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoogleVisionModel implements VisionModel {
    private final HttpClient client;

    private static final ObjectMapper MAPPER;

    @Inject
    private FilesScaledModel filesScaledModel;

    @Inject
    private FilesOriginalModel filesOriginalModel;


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

    public GoogleVisionModel() {
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
    public F.Tuple<List<String>, List<String>> annotate(Photo photo) {
        try {
            File medium = filesScaledModel.get(photo, PhotoResolution.Size.medium, null, null);
            VisionRequests visionRequest = new VisionRequests(
                    new VisionRequest(
                            new VisionImage(medium.getData()),
                            List.of(new VisionFeature("OBJECT_LOCALIZATION", null), new VisionFeature("LABEL_DETECTION", 20))
                    )
            );
            URI uri;
            try {
                uri = new URI("https://vision.googleapis.com/v1/images:annotate?key=" + Config.Option.GOOGLE_MAPS_SERVER_KEY.get());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(visionRequest)))
                    .header(Http.HeaderNames.CONTENT_TYPE, "application/json")
                    .build();
            VisionResponses responses = client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(new TypeReference<VisionResponses>() {
            })).get();
            List<String> labels = responses.responses.get(0).labelAnnotations.stream().map(a -> a.description).collect(Collectors.toUnmodifiableList());

            BoundingPoly train = null;
            if (responses.responses.get(0).localizedObjectAnnotations != null) {
                for (LocalizeObjectAnnotation annotation : responses.responses.get(0).localizedObjectAnnotations) {
                    if (annotation.name.toLowerCase().contains("train")) {
                        train = annotation.boundingPoly;
                        break;
                    }
                }
            }

            if (train == null) {
                return new F.Tuple(labels, List.of());
            }

            double left = 1;
            double right = 0;
            double top = 0;
            double bottom = 1;
            for (NormalizedVertex v : train.normalizedVertices) {
                left = Math.min(left, v.x);
                right = Math.max(right, v.x);
                bottom = Math.min(bottom, v.y);
                top = Math.max(top, v.y);
            }

            File original = filesOriginalModel.get(photo);

            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(original.getData()));

            int x = (int) Math.round(left * inputImage.getWidth());
            int y = (int) Math.round(bottom * inputImage.getHeight());
            int w = (int) Math.round((right - left) * inputImage.getWidth());
            int h = (int) Math.round((top - bottom) * inputImage.getHeight());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(0.7f);
            writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
            writer.write(null, new IIOImage(inputImage.getSubimage(x, y, w, h), null, null), iwp);
            writer.dispose();

            visionRequest = new VisionRequests(
                    new VisionRequest(
                            new VisionImage(byteArrayOutputStream.toByteArray()),
                            List.of(new VisionFeature("TEXT_DETECTION", null))
                    )
            );

            req = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(visionRequest)))
                    .header(Http.HeaderNames.CONTENT_TYPE, "application/json")
                    .build();

            responses = client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(jsonResponseHandler(new TypeReference<VisionResponses>() {
            })).get();

            List<String> texts = new ArrayList<>();
            if (responses.responses.get(0).textAnnotations != null) {
                for (int i = 1; i < responses.responses.get(0).textAnnotations.size(); i++) {
                    // ignore first entry, it is a string with all texts combined
                    texts.add(responses.responses.get(0).textAnnotations.get(i).description);
                }
            }
            return new F.Tuple<>(labels, texts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class VisionRequests {
        final List<VisionRequest> requests;
        public VisionRequests(VisionRequest request) {
            requests = List.of(request);
        }
    }

    private static class VisionRequest {
        final VisionImage image;
        final List<VisionFeature> features;
        public VisionRequest(VisionImage image, List<VisionFeature> features) {
            this.image = image;
            this.features = features;
        }
    }

    private static class VisionImage {
        final String content;
        public VisionImage(byte[] image) {
            content = new String(Base64.encode(image));
        }
    }

    private static class VisionFeature {
        final String type;
        final Integer maxResults;
        public VisionFeature(String type, Integer maxResults) {
            this.type = type;
            this.maxResults = maxResults;
        }
    }

    private static class VisionResponses {
        List<VisionResponse> responses;
    }

    private static class VisionResponse {
        List<VisionLabelAnnotation> labelAnnotations;
        List<LocalizeObjectAnnotation> localizedObjectAnnotations;
        List<VisionTextAnnotation> textAnnotations;
    }

    private static class VisionLabelAnnotation {
        String description;
    }

    private static class LocalizeObjectAnnotation {
        String name;
        BoundingPoly boundingPoly;
    }

    private static class BoundingPoly {
        List<NormalizedVertex> normalizedVertices;
    }

    private static class NormalizedVertex {
        double x;
        double y;
    }

    private static class VisionTextAnnotation {
        String description;
    }
}
