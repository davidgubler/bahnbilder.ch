package utils;

import com.google.inject.Injector;
import org.apache.pekko.stream.Materializer;
import play.libs.streams.Accumulator;
import play.mvc.*;

import javax.inject.Inject;

public class HostnameFilter extends EssentialFilter {
    @Inject
    private Injector injector;

    @Inject
    public HostnameFilter(Materializer mat) {
        super();
    }

    @Override
    public EssentialAction apply(EssentialAction next) {
        return EssentialAction.of(request -> {
            // redirect to main host name without prefixes
            if (Config.Option.HOST_DE.get() != null && request.host().endsWith("." + Config.Option.HOST_DE.get())) {
                String protocol = request.secure() ? "https://" : "http://";
                return Accumulator.done(Results.redirect(protocol + Config.Option.HOST_DE.get() + request.path()));
            }
            if (Config.Option.HOST_EN.get() != null && request.host().endsWith("." + Config.Option.HOST_EN.get())) {
                String protocol = request.secure() ? "https://" : "http://";
                return Accumulator.done(Results.redirect(protocol + Config.Option.HOST_EN.get() + request.path()));
            }
            return next.apply(request);
        });
    }
}
