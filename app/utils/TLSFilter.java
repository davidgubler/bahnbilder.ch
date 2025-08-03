package utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;

import org.apache.pekko.stream.Materializer;
import play.mvc.*;

public class TLSFilter extends Filter {

    @Inject
    public TLSFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next, Http.RequestHeader requestHeader) {
        if (Config.tlsEnabled() && !requestHeader.secure()) {
            String host = requestHeader.host();
            if (host.endsWith(":" + Config.getPlainPort())) {
                // we need to be careful to not break IPv6 addresses in URLs
                host = host.substring(0, host.lastIndexOf(":"));
            }
            String url = "https://" + host + (Config.getTLSPort() == 443 ? "" : ":" + Config.getTLSPort()) + requestHeader.path();
            return CompletableFuture.completedFuture(Results.redirect(url));
        }
        if (Config.tlsEnabled()) {
            return next.apply(requestHeader).thenApply(result -> result.withHeader(Http.HeaderNames.STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains"));
        }
        return next.apply(requestHeader);
    }
}
