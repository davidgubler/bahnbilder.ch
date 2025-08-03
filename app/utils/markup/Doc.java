package utils.markup;

import models.PhotosModel;
import utils.Slice;

import java.util.List;

public class Doc extends ContentNode {
    public Doc(List<Node> content) {
        super(content);
    }

    @Override
    public int match(Slice slice) {
        return -1;
    }

    @Override
    public Node get(Slice slice) {
        return null;
    }

    @Override
    public String toHtml(String lang, PhotosModel photosModel) {
        String html = "";
        for (Node node : getContent()) {
            html += node.toHtml(lang, photosModel);
        }
        return html;
    }

    @Override
    public String toBBCode(String lang, PhotosModel photosModel) {
        String bbCode = "";
        for (Node node : getContent()) {
            bbCode += node.toBBCode(lang, photosModel);
        }
        return bbCode;
    }
}
