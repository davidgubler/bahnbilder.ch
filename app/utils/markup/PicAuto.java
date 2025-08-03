package utils.markup;

import entities.Photo;
import models.PhotosModel;
import play.twirl.api.utils.StringEscapeUtils;
import utils.Config;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicAuto extends Node {
    private static final Pattern OPEN = Pattern.compile("^(\\[picauto:\s*([0-9]+)\\]).*", Pattern.DOTALL);
    private static final Pattern OPEN_LEGACY = Pattern.compile("^(\\[foto:\s*([0-9]+)\\]).*", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

    private int id;

    public PicAuto() {
        super();
    }

    public PicAuto(Matcher m, Slice slice) {
        super();
        id = Integer.parseInt(m.group(2));
    }

    @Override
    public int match(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (m.matches()) {
            return m.group(1).length();
        }
        m = OPEN_LEGACY.matcher(slice);
        if (m.matches()) {
            return m.group(1).length();
        }
        return -1;
    }

    @Override
    public PicAuto get(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (!m.matches()) {
            m = OPEN_LEGACY.matcher(slice);
            if (!m.matches()) {
                throw new IllegalStateException();
            }
        }
        return new PicAuto(m, slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        Photo photo = photosModel.get(id);
        if (photo == null) {
            return "<br /><b>&gt;&gt; PHOTO ID " + id + " NOT FOUND! &lt;&lt;</b><br />\n";
        }
        return Pic.getHtml(photo, StringEscapeUtils.escapeXml11(photo.getGeneratedDescription(lang, true, true, true, true, false)));
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        Photo photo = photosModel.get(id);
        if (photo == null) {
            return "[b]>> PHOTO ID " + id + " NOT FOUND! <<[/b]\n";
        }
        String bbCode = "[url=" + Config.getSelfUrl(lang) + "/" + id + "][img]" + Config.getSelfUrl(lang) + "/photos/large/" + id + ".jpg[/img][/url]\n";
        bbCode += "[i]" + photo.getGeneratedDescription(lang, true, true, true, true, false) + "[/i]\n";
        return bbCode;
    }
}
