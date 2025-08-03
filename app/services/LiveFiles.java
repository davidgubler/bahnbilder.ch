package services;

import controllers.FilesController;
import entities.File;
import entities.Photo;
import entities.tmp.TmpFile;
import play.libs.F;
import play.mvc.Http;
import utils.Config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class LiveFiles {

    private final HttpClient client;

    private String hostname;

    public LiveFiles() {
        hostname = Config.Option.LIVEFILES_HOSTNAME.get();
        client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    private Function<HttpResponse<byte[]>, F.Tuple<TmpFile, TmpFile.Status>> jpegResponseHandler() {
        return httpResponse -> {
            if (httpResponse.statusCode() == 404) {
                return new F.Tuple(null, TmpFile.Status.DELETED);
            }
            if (httpResponse.statusCode() == 304) {
                return new F.Tuple(null, TmpFile.Status.UNMODIFIED);
            }
            if (httpResponse.statusCode() != 200) {
                throw new CompletionException("HTTP response " + httpResponse.statusCode(), null);
            }
            TmpFile tmpFile = new TmpFile(httpResponse.body(), FilesController.parseLastModifiedHeaderValue(httpResponse.headers().firstValue(Http.HeaderNames.LAST_MODIFIED).orElse(null)), httpResponse.headers().firstValue(Http.HeaderNames.ETAG).orElse(null));
            return new F.Tuple(tmpFile, TmpFile.Status.MODIFIED);
        };
    }


    public URI getPublicUri(Photo photo) {
        try {
            return new URI("https://" + hostname + "/pictures/original/" + photo.getId() + ".jpg");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public F.Tuple<TmpFile, TmpFile.Status> get(Photo photo, File existingFile) {
        HttpRequest.Builder b = HttpRequest.newBuilder(getPublicUri(photo));
        if (existingFile != null) {
            b = b.header(Http.HeaderNames.IF_MODIFIED_SINCE, FilesController.getLastModifiedHeaderValue(existingFile.getLastModified()));
        }

        try {
            return client.sendAsync(b.build(), HttpResponse.BodyHandlers.ofByteArray()).thenApply(jpegResponseHandler()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
