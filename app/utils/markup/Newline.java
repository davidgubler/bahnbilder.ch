package utils.markup;

import models.PhotosModel;
import utils.Slice;

public class Newline extends Node {
    private static final String OPEN = "\n";

    public Newline() {
    }

    @Override
    public int match(Slice slice) {
        if (slice.startsWith(OPEN)) {
            return 1;
        }
        return -1;
    }

    @Override
    public Newline get(Slice slice) {
        return this;
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        return "<br />\n";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        return "\n";
    }
}
