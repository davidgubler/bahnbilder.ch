package utils.markup;

import models.PhotosModel;
import utils.InputUtils;
import utils.MarkupParser;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Size extends ContentNode {
    private static final Pattern OPEN = Pattern.compile("^(\\[size:([0-9]+)\\]).*", Pattern.DOTALL);
    private static final String CLOSE = "[/size]";

    private int size;

    public Size() {
        super(null);
    }

    public Size(Matcher m, Slice slice) {
        super(MarkupParser.parse(slice.sub(m.group(1).length(), slice.indexOf(CLOSE))));
        size = InputUtils.toInt(m.group(2).trim());
    }

    @Override
    public int match(Slice slice) {
        if (OPEN.matcher(slice).matches()) {
            int closeIndex = slice.indexOf(CLOSE);
            if (closeIndex > 0) {
                return closeIndex + CLOSE.length();

            }
        }
        return -1;
    }

    @Override
    public Size get(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (!m.matches()) {
            throw new IllegalStateException();
        }
        return new Size(m, slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }
        return "<span style=\"font-size: " + size + "px\">" + html + "</span>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toHtml(lang, photosModel);
        }
        return "[size=" + size + "px]" + bbCode + "[/size]";
    }
}
