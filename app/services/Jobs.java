package services;

import biz.Dns;
import biz.ValidationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import entities.User;
import entities.dummy.DummyUser;
import models.ViewsModel;
import org.apache.pekko.actor.ActorSystem;
import play.mvc.Http;
import utils.BahnbilderLogger;
import utils.Context;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.function.Supplier;

public class Jobs {

    private BahnbilderLogger logger = new BahnbilderLogger(Jobs.class);

    @Inject
    private ViewsModel viewsModel;

    @Inject
    private Dns dns;

    @Inject
    private Injector injector;

    private static final Http.Request REQUEST = new Http.RequestBuilder().remoteAddress("127.0.0.1").build();

    private static final User USER = new DummyUser("BackgroundJob");

    @Inject
    public Jobs(ActorSystem system) {
        system.registerOnTermination(() -> {
            shutDown = true;
        });
        job(system, 4, 0, 0, () -> {
            logger.info(null, "collecting views");
            viewsModel.collect();
            return null;
        });
        job(system, null, null, new Random().nextInt(60), () -> {
            logger.info(REQUEST, "Testing URLs");
            try {
                dns.check(new Context(injector, REQUEST), USER);
            } catch (ValidationException e) {
                throw new RuntimeException(e.getErrors().toString());
            }
            return null;
        });
    }

    private volatile boolean shutDown = false;

    private LocalDateTime next(LocalDateTime now, Integer hour, Integer minute, Integer second) {
        LocalDateTime next = now.truncatedTo(ChronoUnit.SECONDS);
        ChronoUnit smallest = ChronoUnit.DAYS;

        if (hour != null) {
            next = next.withHour(hour);
        } else {
            smallest = ChronoUnit.HOURS;
        }

        if (minute != null) {
            next = next.withMinute(minute);
        } else {
            smallest = ChronoUnit.MINUTES;
        }

        if (second != null) {
            next = next.withSecond(second);
        } else {
            smallest = ChronoUnit.SECONDS;
        }

        while (next.isAfter(now)) {
            next = next.minus(1, smallest);
        }
        while (!next.isAfter(now)) {
            next = next.plus(1, smallest);
        }

        return next;
    }

    private <T> void job(ActorSystem system, Integer hour, Integer minute, Integer second, Supplier<T> f) {
        Thread t = new Thread(() -> {
            while (!shutDown) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime next = next(now, hour, minute, second);
                try {
                    Thread.sleep(ChronoUnit.MILLIS.between(now, next));
                } catch (Exception e) {
                    return;
                }
                try {
                    f.get();
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }
        });
        system.registerOnTermination(t::interrupt);
        t.start();
    }
}
