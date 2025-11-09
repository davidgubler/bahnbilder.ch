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
        String uri = request.uri();
        if (uri.startsWith("/assets/") || uri.startsWith("/favicon.") || uri.startsWith("/lang") || uri.contains("/_")) {
            // filter some technical URIs that aren't interesting at all
            return;
        }
        String referer = request.header("Referer").orElse(null);
        if (referer != null) {
            // reduce size and redundancy
            if (referer.startsWith("http://")) {
                referer = referer.substring(7);
            } else if (referer.startsWith("https://")) {
                referer = referer.substring(8);
            }
            if (referer.endsWith("/")) {
                referer = referer.substring(0, referer.length() - 1);
            }
            if (referer.length() > 256) {
                referer = referer.substring(0, 256);
            }
        }
        Context.get(request).getRequestsDailyModel().track(request.uri(), referer);
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
