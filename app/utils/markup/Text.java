package utils.markup;

import models.PhotosModel;
import play.twirl.api.utils.StringEscapeUtils;
import utils.Slice;

public class Text extends Node {

    private final String text;

    public Text(Slice slice) {
        this.text = slice.toString();
    }

    public int match(Slice slice) {
        return -1;
    }

    @Override
    public Text get(Slice slice) {
        return null;
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        return StringEscapeUtils.escapeXml11(text);
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        return text;
    }
}
