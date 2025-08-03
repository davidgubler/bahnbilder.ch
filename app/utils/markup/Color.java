package utils.markup;

import models.PhotosModel;
import play.twirl.api.utils.StringEscapeUtils;
import utils.MarkupParser;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color extends ContentNode {
    private static final Pattern OPEN = Pattern.compile("^(\\[color:([^]]+)\\]).*", Pattern.DOTALL);
    private static final String CLOSE = "[/color]";

    private String color;

    public Color() {
        super(null);
    }

    public Color(Matcher m, Slice slice) {
        super(MarkupParser.parse(slice.sub(m.group(1).length(), slice.indexOf(CLOSE))));
        color = m.group(2).trim();
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
    public Color get(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (!m.matches()) {
            throw new IllegalStateException();
        }
        return new Color(m, slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }
        return "<span style=\"color: " + StringEscapeUtils.escapeXml11(color) + "\">" + html + "</span>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toBBCode(lang, photosModel);
        }
        return "[color=" + color + "]" + bbCode + "[/color]";
    }
}
