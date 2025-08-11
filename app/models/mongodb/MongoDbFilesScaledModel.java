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
import models.FilesOriginalModel;
import models.FilesScaledModel;
import play.libs.F;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

import java.util.Iterator;
import java.util.List;

public class MongoDbFilesScaledModel extends MongoDbModel<MongoDbFile> implements FilesScaledModel {

    @Inject
    private FilesOriginalModel filesOriginalModel;

    @Override
    protected Datastore getDs() {
        return mongoDb.getDsFilesScaled();
    }

    @Override
    public String getPublic(Photo photo, PhotoResolution.Size size) {
        return "/photos/" + size.name() + "/" + photo.getId() + ".jpg";
    }

    private File create(PhotoResolution.Size size, File fromOriginal) {
        byte[] scaledData;
        try {
            scaledData = scale(fromOriginal, size);
        } catch (IOException e) {
            throw new RuntimeException("Error scaling original:" + fromOriginal.getPhotoId(), e);
        }

        if (scaledData == null) {
            // too small to be scaled to this size
            return fromOriginal;
        }
        MongoDbFile file = new MongoDbFile(fromOriginal.getPhotoId(), size, fromOriginal.getLastModified(), fromOriginal.getEtag(), scaledData);
        getDs().save(file);
        return file;
    }

    private File update(File scaled, File fromOriginal) {
        byte[] scaledData;
        try {
            scaledData = scale(fromOriginal, scaled.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Error re-scaling original:" + fromOriginal.getPhotoId(), e);
        }
        if (scaledData == null) {
            // too small to be scaled to this size
            return fromOriginal;
        }
        ((MongoDbFile)scaled).setLastModified(fromOriginal.getLastModified());
        ((MongoDbFile)scaled).setEtag(fromOriginal.getEtag());
        ((MongoDbFile)scaled).setData(scaledData);
        query(scaled).update(new UpdateOptions(), UpdateOperators.set("lastModified", scaled.getLastModified()), UpdateOperators.set("etag", scaled.getEtag()), UpdateOperators.set("data", scaled.getData()));
        return scaled;
    }

    private static final float[] SHARPEN_KERNEL = { -0.00f, -0.15f, -0.00f,
                                                    -0.15f,  1.60f, -0.15f,
                                                    -0.00f, -0.15f, -0.00f};

    private byte[] scale(File original, PhotoResolution.Size size) throws IOException {
        BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(original.getData()));
        if (inputImage == null) {
            throw new IOException("ImageIO couldn't extract image (returned null)");
        }
        int originalWidth = inputImage.getWidth();
        int originalHeight = inputImage.getHeight();
        F.Tuple<Integer, Integer> newSize = size.getScaledSize(originalWidth, originalHeight);
        int newWidth = newSize._1;
        int newHeight = newSize._2;

        if (newWidth == originalWidth && newHeight == originalHeight) {
            return null;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, newWidth, newHeight, null);

        Kernel kernel = new Kernel(3, 3, SHARPEN_KERNEL);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage sharpenedImage = new BufferedImage(resizedImage.getWidth(), resizedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        cop.filter(resizedImage, sharpenedImage);

        g2d.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(0.93f);
        iwp.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
        writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
        writer.write(null, new IIOImage(sharpenedImage, null, null), iwp);
        writer.dispose();

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public File get(Photo photo, PhotoResolution.Size size, Instant ifModifiedSince, String ifNoneMatch) {
        if (photo == null) {
            return null;
        }

        // Decision tree:
        //
        //  if no etag/date provided: fetch scaled version, fetch original using etag from scaled version
        //     if no original returned: scaled version is current, return it
        //     if original is returned: scaled version is outdated, rescale, return new version
        // if etag/date provided: fetch original
        //   if no original returned (etag/date matches): fetch scaled version with etag/date
        //     if no scaled version returned (etag/date matches): browser has correct data, return not modified
        //     if scaled version returned (etag/date diverges): browser has incorrect data, scaled version is outdated, rescale, return new version
        //   if original returned (etag/date diverges): browser has incorrect data, fetch scaled version unconditionally
        //     if scaled version doesn't exist: create scaled version, return
        //     if scaled version exists:
        //       if etag of scaled version equals etag of original: return scaled version
        //       if etag of scaled version differs from original: scaled version is outdated, rescale, return new version

        if (ifModifiedSince == null && ifNoneMatch == null) {
            // no etag/date provided
            File scaled = query().filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", size.name())).first();
            if (scaled != null) {
                File original = filesOriginalModel.get(photo, null, scaled.getEtag());
                return original == null ? scaled : update(scaled, original);
            }
            return scaled == null ? create(size, filesOriginalModel.get(photo)) : scaled;
        }

        // etag/date provided
        File original = filesOriginalModel.get(photo, ifModifiedSince, ifNoneMatch);
        if (original == null) {
            // fetch scaled version with etag/date
            Query<MongoDbFile> query = query().filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", size.name()));
            if (ifModifiedSince != null) {
                query = query.filter(Filters.gt("lastModified", ifModifiedSince));
            }
            if (ifNoneMatch != null) {
                query = query.filter(Filters.ne("etag", ifNoneMatch));
            }
            File scaled = query.first();
            return scaled == null ? null : update(scaled, filesOriginalModel.get(photo));
        } else {
            // browser has incorrect data, fetch scaled version unconditionally
            Query<MongoDbFile> query = query().filter(Filters.eq("photoId", photo.getId()), Filters.eq("size", size.name()));
            File scaled = query.first();
            if (scaled == null) {
                return create(size, original);
            } else {
                return scaled.getEtag().equals(original.getEtag()) ? scaled : update(scaled, original);
            }
        }
    }

    @Override
    public void delete(List<Integer> photoIds) {
        query().filter(Filters.in("photoId", photoIds)).delete(new DeleteOptions().multi(true));
    }
}
