package utils.markup;

import models.PhotosModel;
import utils.MarkupParser;
import utils.Slice;

public class Italic extends ContentNode {
    private static final String OPEN = "[i]";
    private static final String CLOSE = "[/i]";

    public Italic() {
        super(null);
    }

    public Italic(Slice slice) {
        super(MarkupParser.parse(slice.sub(OPEN.length(), slice.length() - CLOSE.length())));
    }

    public int match(Slice slice) {
        if (slice.startsWith(OPEN)) {
            int closeIndex = slice.indexOf(CLOSE);
            if (closeIndex > 0) {
                return closeIndex + CLOSE.length();
            }
        }
        return -1;
    }

    @Override
    public Italic get(Slice slice) {
        return new Italic(slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }
        return "<i>" + html + "</i>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toHtml(lang, photosModel);
        }
        return "[i]" + bbCode + "[/i]";
    }
}
