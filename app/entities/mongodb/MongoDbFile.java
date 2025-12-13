package entities.mongodb;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.jpeg.JpegDirectory;
import dev.morphia.annotations.*;
import entities.File;
import entities.PhotoResolution;
import org.bson.types.ObjectId;
import utils.BahnbilderLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Entity(value = "files", useDiscriminator = false)
@Indexes({@Index(options = @IndexOptions(unique = true), fields = {@Field(value = "photoId"), @Field(value = "size")})})
public class MongoDbFile implements MongoDbEntity, File {

    @Transient
    private BahnbilderLogger logger = new BahnbilderLogger(MongoDbFile.class);

    @Id
    private ObjectId _id;

    private int photoId;

    private String size;

    private Instant lastModified;

    private String etag;

    private byte[] data;

    public MongoDbFile() {
        // dummy for Morphia
    }

    public MongoDbFile(int photoId, PhotoResolution.Size size, Instant lastModified, String etag, byte[] data) {
        this.photoId = photoId;
        this.size = size == PhotoResolution.Size.original ? null : size.name();
        this.lastModified = lastModified;
        this.etag = etag;
        this.data = data;
    }

    @Override
    public int getPhotoId() {
        return photoId;
    }

    @Override
    public PhotoResolution.Size getSize() {
        return size == null ? PhotoResolution.Size.original : PhotoResolution.Size.valueOf(size);
    }

    @Override
    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public PhotoResolution getResolution() {
        try (InputStream in = new ByteArrayInputStream(getData())) {
            Metadata metadata = ImageMetadataReader.readMetadata(in);
            Integer width = null;
            Integer height = null;

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (directory instanceof JpegDirectory) {
                        if (tag.getTagType() == 1) {
                            try {
                                height = Integer.parseInt(tag.getDescription().split(" ")[0]);
                            } catch (Exception e) {
                                logger.info(null, "Photo " + photoId + " has unparseable data in JpegDirectory/1 (height): " + e.getMessage());
                            }
                        }
                        if (tag.getTagType() == 3) {
                            try {
                                width = Integer.parseInt(tag.getDescription().split(" ")[0]);
                            } catch (Exception e) {
                                logger.info(null, "Photo " + photoId + " has unparseable data in JpegDirectory/3 (width): " + e.getMessage());
                            }
                        }
                    }
                }
            }

            if (width == null || height == null) {
                return null;
            }
            return new PhotoResolution(getSize(), width, height);
        } catch (IOException e){
            // photo may not exist in this resolution
            //BahnbilderLogger.info(null, "Photo " + url + " could not be downloaded: " + e.getMessage());
            //return null;
            throw new RuntimeException(e); // doesn't happen
        } catch (ImageProcessingException e){
            return null;
        }
    }

    @Override
    public ObjectId getObjectId() {
        return _id;
    }
}
