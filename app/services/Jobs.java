package services;

import com.google.inject.Inject;
import models.ViewsModel;
import org.apache.pekko.actor.ActorSystem;
import utils.BahnbilderLogger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

public class Jobs {

    @Inject
    private ViewsModel viewsModel;

    @Inject
    public Jobs(ActorSystem system) {
        system.registerOnTermination(() -> {
            shutDown = true;
        });
        job(system, 4, 0, 0, () -> {
            BahnbilderLogger.info(null, "collecting views");
            viewsModel.collect();
            return null;
        });
    }

    private volatile boolean shutDown = false;

    private LocalDateTime next(LocalDateTime now, Integer hour, Integer minute, Integer second) {
        LocalDateTime next = now.truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);
        if (hour != null) {
            next = next.withHour(hour);
            if (now.getHour() > hour) {
                next = next.plusDays(1);
            }
        }
        if (minute != null) {
            next = next.withMinute(minute);
            if (now.getMinute() > minute) {
                next = next.plusHours(1);
            }
        }
        if (second != null) {
            next = next.withSecond(second);
            if (now.getSecond() > second) {
                next = next.plusMinutes(1);
            }
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
                    BahnbilderLogger.error(null, e);
                }
            }
        });
        system.registerOnTermination(t::interrupt);
        t.start();
    }
}
