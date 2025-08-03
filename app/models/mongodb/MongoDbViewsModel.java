package models.mongodb;

import com.google.inject.Inject;
import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.Views;
import entities.mongodb.MongoDbViews;
import models.PhotosModel;
import models.ViewsModel;
import play.mvc.Http;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MongoDbViewsModel extends MongoDbModel<MongoDbViews> implements ViewsModel {
    @Inject
    private PhotosModel photosModel;

    @Override
    public void countView(int photoId, Http.Request request) {
        if (!mongoDb.isWritable()) {
            // counting views isn't all that important
            return;
        }
        Optional<String> userAgent = request.header("User-Agent");
        if (userAgent.isPresent()) {
            String ua = userAgent.get().toLowerCase(Locale.ROOT);
            if (ua.contains("bot") || ua.contains("spider")) {
                return;
            }
        }

        InetAddress addr;
        try {
            addr = InetAddress.getByName(request.remoteAddress());
            if (addr instanceof Inet6Address) {
                byte[] bytes = addr.getAddress();
                // We only count one view per /64 to account for stuff like IPv6 privacy extensions.
                // This is kind of similar to how we count in case of IPv4 with multiple hosts behind a single IP;
                // Both may under-represent the true view count.
                bytes[8] = 0;
                bytes[9] = 0;
                bytes[10] = 0;
                bytes[11] = 0;
                bytes[12] = 0;
                bytes[13] = 0;
                bytes[14] = 0;
                bytes[15] = 0;
                addr = Inet6Address.getByAddress(bytes);
            }
        } catch (UnknownHostException e) {
            // not parseable, ignore
            return;
        }
        LocalDate today = LocalDate.now();
        MongoDbViews views = query().filter(Filters.eq("photoId", photoId), Filters.eq("date", today)).first();
        if (views == null) {
            try {
                getDs().insert(new MongoDbViews(photoId, today, List.of(addr.getHostAddress())));
                return;
            } catch (Exception e) {
                // somebody else may have been faster, try adding IP to existing document
            }
        }

        query().filter(Filters.eq("photoId", photoId), Filters.eq("date", today)).update(new UpdateOptions(), UpdateOperators.addToSet("ips", addr.getHostAddress()));
    }

    /**
     * Move the collected views into the view counters of the photos.
     * This method is written such that it can be executed concurrently by multiple application instances on the same DB, no locking needed.
     * Note that this method isn't safe in the sense that it could crash in the middle and loose data. But since none of this is critical functionality we're just ignoring that.
     */
    @Override
    public void collect() {
        LocalDate today = LocalDate.now();
        while (true) {
            Views views = query().filter(Filters.lt("date", today)).stream().findAny().orElse(null);
            if (views == null) {
                break;
            }
            if (query(views).delete().getDeletedCount() > 0) {
                // we've actually deleted the views object, hence we're responsible for updating the views of the corresponding photo
                photosModel.addViews(views.getPhotoId(), views.getIps().size());
            } else {
                // somebody else was quicker, do nothing
            }
        }
    }

    @Override
    public int getUncollected(int photoId) {
        return query().filter(Filters.eq("photoId", photoId)).stream().map(v -> v.getIps().size()).reduce(Integer::sum).orElse(0);
    }
}
