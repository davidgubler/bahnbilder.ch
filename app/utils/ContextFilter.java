package utils;

import com.google.inject.Injector;
import org.apache.pekko.stream.Materializer;
import play.libs.F;
import play.libs.typedmap.TypedKey;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class ContextFilter extends Filter {

    private static BahnbilderLogger logger = new BahnbilderLogger(ContextFilter.class);

    @Inject
    private Injector injector;

    @Inject
    public ContextFilter(Materializer mat) {
        super(mat);
    }

    public static TypedKey<Context> CONTEXT = TypedKey.create("context");

    private static volatile long slowRequests = 0l;

    public static void log(Http.RequestHeader request, long startTime) {
        Context context = Context.get(request);
        long endTime = System.currentTimeMillis();

        Map<String, F.Tuple<Integer, Integer>> callSummary = context.getCallSummary();

        List<String> models = new LinkedList<>(callSummary.keySet());
        Collections.sort(models);

        List<String> summaries = new LinkedList<>();
        int modelSum = 0;
        for (String model : models) {
            if (callSummary.get(model)._1 == 0) {
                continue;
            }
            summaries.add(model + ": " + callSummary.get(model)._1 + " calls/" + callSummary.get(model)._2 + " ms");
            modelSum += callSummary.get(model)._2;
        }

        summaries.add("unaccounted: " + (endTime - startTime - modelSum) + " ms");

        String subsystemsSummary = StringUtils.join(summaries, ", ");
        if ((endTime - startTime) > 100) {
            logger.info(request, request.method() + " " + request.path() + " took " + (endTime - startTime) + " ms; " + subsystemsSummary);
            slowRequests++;
        }
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        Context context = new Context(injector, requestHeader);
        long startTime = System.currentTimeMillis();
        final Http.RequestHeader requestHeaderWithContext = requestHeader.withAttrs(requestHeader.attrs().put(CONTEXT, context));
        return nextFilter.apply(requestHeaderWithContext).thenApply(result -> { log(requestHeaderWithContext, startTime); return result; });
    }

    public static long getSlowRequests() {
        return slowRequests;
    }
}
