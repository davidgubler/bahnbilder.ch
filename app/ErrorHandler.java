import play.http.HttpErrorHandler;
import play.mvc.*;
import play.mvc.Http.*;
import utils.AlreadyInProgressException;
import utils.NotAllowedException;
import utils.NotFoundException;
import utils.BahnbilderLogger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    private BahnbilderLogger logger = new BahnbilderLogger(ErrorHandler.class);

    private String extractLang(RequestHeader request) {
        if (request.host() == null || request.host().endsWith("bahnbilder.ch")) {
            return "de";
        }
        return "en";
    }

    public CompletionStage<Result> onClientError(
            RequestHeader request, int statusCode, String message) {
        if (statusCode == 404 || (statusCode == 400 && message.startsWith("Cannot parse parameter "))) {
            return CompletableFuture.completedFuture(Results.notFound(views.html.error.render(request, "404: Not Found", extractLang(request))));
        }
        return CompletableFuture.completedFuture(Results.status(statusCode, views.html.error.render(request, message, extractLang(request))));
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        if (exception instanceof CompletionException) {
            exception = exception.getCause();
        }
        if (exception instanceof NotFoundException) {
            return CompletableFuture.completedFuture(Results.notFound(views.html.error.render(request, "404: " + exception.getMessage(), extractLang(request))));
        }
        if (exception instanceof NotAllowedException) {
            return CompletableFuture.completedFuture(Results.forbidden(views.html.error.render(request, "403: " + exception.getMessage(), extractLang(request))));
        }
        if (exception instanceof AlreadyInProgressException) {
            return CompletableFuture.completedFuture(Results.internalServerError(views.html.error.render(request, "500: " + exception.getMessage(), extractLang(request))));
        }
        logger.error(request, exception);
        return CompletableFuture.completedFuture(Results.internalServerError(views.html.error.render(request, "500: Whoops, something went wrong", extractLang(request))));
    }
}