package utils;

import com.google.inject.Injector;
import org.apache.pekko.stream.Materializer;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class AccessLogFilter extends Filter {

    private BahnbilderLogger logger = new BahnbilderLogger(AccessLogFilter.class);

    @Inject
    private Injector injector;

    @Inject
    public AccessLogFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        return nextFilter.apply(requestHeader).thenApply(result -> {
            logger.access(requestHeader, result);
            return result;
        });
    }
}
