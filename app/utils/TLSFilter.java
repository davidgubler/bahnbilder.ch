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
        // this broke on 2026-04-28 - Maybe Cloudflare started to send different headers? Whatever the reason, requestHeader.secure() now returns false despite
        // "X-Forwarded-Proto: https" and the request coming in via the HTTPS port. Looks like a Play framework bug.
        // Unfortunately there doesn't seem to be an alternative way to determine if the incoming request is using TLS. But we don't actually need this logic
        // because Cloudflare already does the TLS redirect.
        /*if (Config.tlsEnabled() && !requestHeader.secure()) {
            String host = requestHeader.host();
            if (host.endsWith(":" + Config.getPlainPort())) {
                // we need to be careful to not break IPv6 addresses in URLs
                host = host.substring(0, host.lastIndexOf(":"));
            }
            String url = "https://" + host + (Config.getTLSPort() == 443 ? "" : ":" + Config.getTLSPort()) + requestHeader.path();
            return CompletableFuture.completedFuture(Results.redirect(url));
        }*/
        if (Config.tlsEnabled()) {
            return next.apply(requestHeader).thenApply(result -> result.withHeader(Http.HeaderNames.STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains"));
        }
        return next.apply(requestHeader);
    }
}
