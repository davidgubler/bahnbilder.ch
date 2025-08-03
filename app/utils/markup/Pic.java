package utils.markup;

import entities.Photo;
import models.PhotosModel;
import utils.Config;
import utils.MarkupParser;
import utils.Slice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pic extends ContentNode {
    private static final Pattern OPEN = Pattern.compile("^(\\[pic:\s*([0-9]+)\\]).*", Pattern.DOTALL);
    private static final String CLOSE = "[/pic]";

    private int id;

    public Pic() {
        super(null);
    }

    public Pic(Matcher m, Slice slice) {
        super(MarkupParser.parse(slice.sub(m.group(1).length(), slice.length() - CLOSE.length())));
        id = Integer.parseInt(m.group(2));
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
    public Pic get(Slice slice) {
        Matcher m = OPEN.matcher(slice);
        if (!m.matches()) {
            throw new IllegalStateException();
        }
        return new Pic(m, slice);
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }

        Photo photo = photosModel.get(id);
        if (photo == null) {
            return "<br /><b>&gt;&gt; PHOTO ID " + id + " NOT FOUND! &lt;&lt;</b><br />\n";
        }

        return getHtml(photo, html);
    }

    protected static String getHtml(Photo photo, String descriptionHtml) {
        int width = photo.getResXLarge().getWidth();
        int height = photo.getResXLarge().getHeight();

        String imgHtml = "<img width=\"" + width + "\" height=\"" + height + "\" style=\"max-width: " + width + "px; height: auto;\" sizes=\"(min-width: 768px) calc(min(100vw - 160px, 1600px)), 100vw\" srcset=\"" + photo.getSrcSet() + "\" src=\"" + photo.getUrlPublic1600() + "\" />";
        String aHtml = "<a itemprop=\"url\" href=\"/" + photo.getId() + "?menu=travelogues\">" + imgHtml + "</a>";
        String divHtml = "<div class=\"inlineimage\" itemprop=\"sharedContent\" itemscope itemtype=\"http://schema.org/ImageObject\">" + aHtml + "<i itemprop='caption' class='caption'>" + descriptionHtml + "</i></div>";
        return divHtml;
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        Photo photo = photosModel.get(id);
        if (photo == null) {
            return "[b]>> PHOTO ID " + id + " NOT FOUND! <<[/b]\n";
        }
        String bbCode = "[url=" + Config.getSelfUrl(lang) + "/" + id + "][img]" + Config.getSelfUrl(lang) + "/photos/large/" + id + ".jpg[/img][/url]\n";
        bbCode += "[i]";
        for (Node node : getContent()) {
            bbCode += node.toBBCode(lang, photosModel);
        }
        bbCode += "[/i]\n";
        return bbCode;
    }
}
