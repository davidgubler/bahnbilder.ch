package controllers;

import entities.File;
import entities.Photo;
import entities.PhotoResolution;
import org.apache.pekko.util.ByteString;
import play.http.HttpEntity;
import play.mvc.*;
import utils.Context;
import utils.NotFoundException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class FilesController extends Controller {
    private final String IMAGE_JPEG = "image/jpeg";

    private static final DateTimeFormatter LAST_MODIFIED_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").localizedBy(Locale.ENGLISH).withZone(ZoneOffset.UTC);

    public static String getLastModifiedHeaderValue(Instant instant) {
        return LAST_MODIFIED_FORMAT.format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    public static Instant parseLastModifiedHeaderValue(String lastModified) {
        try {
            return LAST_MODIFIED_FORMAT.parse(lastModified, Instant::from);
        } catch (Exception e) {
            return null;
        }
    }

    public Instant getIfLastModifiedSince(Http.Request request) {
        return parseLastModifiedHeaderValue(request.header(Http.HeaderNames.IF_MODIFIED_SINCE).orElse(null));
    }

    public Result original(Http.Request request, Integer photoId) {
        Context context = Context.get(request);
        Instant ifModifiedSince = getIfLastModifiedSince(request);
        String ifNoneMatch = request.header(Http.HeaderNames.IF_NONE_MATCH).orElse(null);

        Photo photo = context.getPhotosModel().get(photoId);
        if (photo == null) {
            throw new NotFoundException("Photo not found");
        }
        File original;
        if (ifModifiedSince == null && ifNoneMatch == null) {
            original = context.getFilesOriginalModel().get(photo);
            if (original == null) {
                throw new NotFoundException("Photo not found");
            }
        } else {
            original = context.getFilesOriginalModel().get(photo, ifModifiedSince, ifNoneMatch);
            if (original == null) {
                return new StatusHeader(NOT_MODIFIED);
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(Http.HeaderNames.CONTENT_DISPOSITION, "inline");
        headers.put(Http.HeaderNames.ETAG, original.getEtag());
        headers.put(Http.HeaderNames.LAST_MODIFIED, getLastModifiedHeaderValue(original.getLastModified()));

        return new Result(
                new play.mvc.ResponseHeader(200, headers),
                new HttpEntity.Strict(ByteString.fromArray(original.getData()), Optional.of(IMAGE_JPEG)));
    }

    public Result scaled(Http.Request request, Integer photoId, PhotoResolution.Size size) {
        Context context = Context.get(request);
        Instant ifModifiedSince = getIfLastModifiedSince(request);
        String ifNoneMatch = request.header(Http.HeaderNames.IF_NONE_MATCH).orElse(null);

        Photo photo = context.getPhotosModel().get(photoId);
        if (photo == null) {
            throw new NotFoundException("Photo not found");
        }
        File scaled;
        if (ifModifiedSince == null && ifNoneMatch == null) {
            scaled = context.getFilesScaledModel().get(photo, size, null, null);
            if (scaled == null) {
                throw new NotFoundException("Photo not found");
            }
        } else {
            scaled = context.getFilesScaledModel().get(photo, size, ifModifiedSince, ifNoneMatch);
            if (scaled == null) {
                return new StatusHeader(NOT_MODIFIED);
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(Http.HeaderNames.CONTENT_DISPOSITION, "inline");
        headers.put(Http.HeaderNames.ETAG, scaled.getEtag());
        headers.put(Http.HeaderNames.LAST_MODIFIED, getLastModifiedHeaderValue(scaled.getLastModified()));

        return new Result(
                new play.mvc.ResponseHeader(200, headers),
                new HttpEntity.Strict(ByteString.fromArray(scaled.getData()), Optional.of(IMAGE_JPEG)));
    }

    public Result small(Http.Request request, Integer photoId) {
        return scaled(request, photoId, PhotoResolution.Size.small);
    }

    public Result medium(Http.Request request, Integer photoId) {
        return scaled(request, photoId, PhotoResolution.Size.medium);
    }

    public Result large(Http.Request request, Integer photoId) {
        return scaled(request, photoId, PhotoResolution.Size.large);
    }

    public Result xlarge(Http.Request request, Integer photoId) {
        return scaled(request, photoId, PhotoResolution.Size.xlarge);
    }

    public Result xxlarge(Http.Request request, Integer photoId) {
        return scaled(request, photoId, PhotoResolution.Size.xxlarge);
    }
}
