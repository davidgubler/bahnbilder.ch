package models.mongodb;

import com.google.inject.Inject;
import dev.morphia.Datastore;
import dev.morphia.DeleteOptions;
import dev.morphia.UpdateOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.File;
import entities.Photo;
import entities.PhotoResolution;
import entities.mongodb.MongoDbFile;
import entities.tmp.TmpFile;
import models.FilesOriginalModel;
import play.libs.F;
import services.LiveFiles;
import utils.Config;
import utils.SimpleDigest;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class MongoDbFilesOriginalModel extends MongoDbModel<MongoDbFile> implements FilesOriginalModel {

    @Inject
    private LiveFiles liveFiles;

    @Override
    protected Datastore getDs() {
        return mongoDb.getDsFilesOriginal();
    }

    @Override
    public String getPublic(Photo photo) {
        return "/photos/original/" + photo.getId() + ".jpg";
    }

    @Override
    public MongoDbFile create(int photoId, byte[] data) {
        String etag = new String(Base64.getEncoder().encode(new SimpleDigest().hash(data)));
        MongoDbFile file = new MongoDbFile(photoId, PhotoResolution.Size.original, Instant.now(), etag, data);
        getDs().save(file);
        return file;
    }

    @Override
    public MongoDbFile create(int photoId, File tmpFile) {
        String etag = tmpFile.getEtag() == null ? new String(Base64.getEncoder().encode(new SimpleDigest().hash(tmpFile.getData()))) : tmpFile.getEtag();
        Instant lastModified = tmpFile.getLastModified() == null ? Instant.now() : tmpFile.getLastModified();
        MongoDbFile file = new MongoDbFile(photoId, PhotoResolution.Size.original, lastModified, etag, tmpFile.getData());
        getDs().save(file);
        return file;
    }

    @Override
    public void update(int photoId, byte[] data) {
        String etag = new String(Base64.getEncoder().encode(new SimpleDigest().hash(data)));
        Instant now = Instant.now();
        query().filter(Filters.eq("photoId", photoId)).update(new UpdateOptions(), UpdateOperators.set("data", data), UpdateOperators.set("etag", etag), UpdateOperators.set("lastModified", now));
    }

    @Override
    public void update(int photoId, File tmpFile) {
        String etag = tmpFile.getEtag() == null ? new String(Base64.getEncoder().encode(new SimpleDigest().hash(tmpFile.getData()))) : tmpFile.getEtag();
        Instant lastModified = tmpFile.getLastModified() == null ? Instant.now() : tmpFile.getLastModified();
        query().filter(Filters.eq("photoId", photoId)).update(new UpdateOptions(), UpdateOperators.set("data", tmpFile.getData()), UpdateOperators.set("etag", etag), UpdateOperators.set("lastModified", lastModified));
    }

    @Override
    public File get(Photo photo) {
        if (photo == null) {
            return null;
        }
        MongoDbFile file = query().filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", null)).first();
        if (file == null && liveFiles != null) {
            F.Tuple<TmpFile, TmpFile.Status> live = liveFiles.get(photo, file);
            if (live._2 == TmpFile.Status.MODIFIED) {
                file = create(photo.getId(), live._1);
            }
        }
        return file;
    }

    @Override
    public TmpFile.Status ensureMigrated(Photo photo) {
        if (liveFiles == null) {
            throw new IllegalStateException("Migration needs LiveFiles to be configured (environment variable " + Config.Option.LIVEFILES_HOSTNAME.name() + ")");
        }
        MongoDbFile file = query().filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", null)).first();
        F.Tuple<TmpFile, TmpFile.Status> live = liveFiles.get(photo, file);
        if (live._2 == TmpFile.Status.MODIFIED) {
            if (file == null) {
                create(photo.getId(), live._1);
                return TmpFile.Status.NEW;
            } else {
                update(photo.getId(), live._1);
                return TmpFile.Status.MODIFIED;
            }
        }
        if (live._2 == TmpFile.Status.DELETED) {
            delete(photo.getId());
            return TmpFile.Status.DELETED;
        }
        return TmpFile.Status.UNMODIFIED;
    }

    @Override
    public File get(Photo photo, Instant ifModifiedSince, String ifNoneMatch) {
        if (ifModifiedSince == null && ifNoneMatch == null) {
            return get(photo);
        }
        if (photo == null) {
            return null;
        }
        Query<MongoDbFile> query = query();
        if (ifModifiedSince != null) {
            query = query.filter(Filters.gt("lastModified", ifModifiedSince));
        }
        if (ifNoneMatch != null) {
            query = query.filter(Filters.ne("etag", ifNoneMatch));
        }
        return query.filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", null)).first();  // null -> not changed relative to etag/lastModified
    }

    @Override
    public void delete(List<Integer> photoIds) {
        query().filter(Filters.in("photoId", photoIds)).delete(new DeleteOptions().multi(true));
    }
}
