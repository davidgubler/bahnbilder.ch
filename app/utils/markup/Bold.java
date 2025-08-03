package utils.markup;

import models.PhotosModel;
import utils.MarkupParser;
import utils.Slice;

public class Bold extends ContentNode {
    private static final String OPEN = "[b]";
    private static final String CLOSE = "[/b]";

    public Bold() {
        super(null);
    }

    public Bold(Slice slice) {
        super(MarkupParser.parse(slice.sub(OPEN.length(), slice.length() - CLOSE.length())));
    }

    @Override
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
    public Bold get(Slice slice) {
        return new Bold(slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }
        return "<b>" + html + "</b>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toBBCode(lang, photosModel);
        }
        return "[b]" + bbCode + "[/b]";
    }
}
