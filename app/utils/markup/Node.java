package utils.markup;

import models.PhotosModel;
import utils.Slice;

public abstract class Node {
    public abstract int match(Slice slice);
    public abstract Node get(Slice slice);

    public abstract String toHtml(String lang, PhotosModel photosModel);

    public abstract String toBBCode(String lang, PhotosModel photosModel);
}
