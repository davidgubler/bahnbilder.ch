package utils.markup;

import models.PhotosModel;
import play.twirl.api.utils.StringEscapeUtils;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url extends Node {
    private static final Pattern OPEN = Pattern.compile("^(\\[url:(https?://[^]]+)\\]).*", Pattern.DOTALL);
    private static final String CLOSE = "[/url]";

    private String url;
    private String text;

    public Url() {

    }

    public Url(Matcher m, Slice slice) {
        url = m.group(2).trim();
        text = slice.sub(m.group(1).length(), slice.length() - CLOSE.length()).toString();
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
    public Url get(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (!m.matches()) {
            throw new IllegalStateException();
        }
        return new Url(m, slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        return "<a target=\"_blank\" href=\"" + StringEscapeUtils.escapeXml11(url) + "\">" + StringEscapeUtils.escapeXml11(text) + "</a>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        return "[url=" + url + "]" + text + "[/url]";
    }
}
