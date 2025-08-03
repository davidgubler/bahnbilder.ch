package utils.markup;

import models.PhotosModel;
import play.twirl.api.utils.StringEscapeUtils;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Link extends Node {
    private static final Pattern OPEN = Pattern.compile("^(https?://\\S+).*", Pattern.DOTALL);

    private String url;

    public Link() {
    }

    public Link(String url) {
        this.url = url;
    }

    @Override
    public int match(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (m.matches()) {
            String url = m.group(1);
            while (url.endsWith(",") || url.endsWith(";") || url.endsWith(".") || url.endsWith(":") || url.endsWith(")") || url.endsWith("]") || url.endsWith(">")) {
                url = url.substring(0, url.length() - 1);
            }
            return url.length();
        }
        return -1;
    }

    @Override
    public Link get(Slice slice) {
        return new Link(slice.toString());
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String urlHtml = StringEscapeUtils.escapeXml11(url);
        return "<a target=\"_blank\" href=\"" + urlHtml + "\">" + urlHtml + "</a>";
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        return "[url=" + url + "]" + url + "[/url]";
    }
}
