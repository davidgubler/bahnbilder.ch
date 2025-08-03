package utils.markup;

import models.PhotosModel;
import utils.MarkupParser;
import utils.Slice;

public class Title extends ContentNode {
    private static final String OPEN = "[title]";
    private static final String CLOSE = "[/title]";

    public Title() {
        super(null);
    }

    public Title(Slice slice) {
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
    public Title get(Slice slice) {
        return new Title(slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }

        return "<h1>" + html + "</h1>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toBBCode(lang, photosModel);
        }
        return "[b][size:16px]" + bbCode + "[/size][/b]";
    }
}
