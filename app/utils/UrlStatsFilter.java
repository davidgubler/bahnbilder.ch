package utils;

import com.google.inject.Injector;
import org.apache.pekko.stream.Materializer;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class UrlStatsFilter extends Filter {
    @Inject
    private Injector injector;

    @Inject
    public UrlStatsFilter(Materializer mat) {
        super(mat);
    }

    public static void stats(Http.RequestHeader request) {
        if (request.uri().startsWith("/assets/") || request.uri().startsWith("/favicon.")) {
            return;
        }
        Context.get(request).getRequestsDailyModel().track(request.uri(), request.header("Referer").orElse(null));
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        try {
            stats(requestHeader);
        } catch (Exception e) {
            BahnbilderLogger.info(requestHeader, "url stats failed: " + e.getMessage());
        }
        return nextFilter.apply(requestHeader);
    }
}
