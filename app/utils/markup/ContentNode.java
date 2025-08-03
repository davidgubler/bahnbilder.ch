package utils.markup;

import java.util.Collections;
import java.util.List;

public abstract class ContentNode extends Node {
    private List<Node> content;

    public ContentNode(List<Node> content) {
        this.content = content;
    }

    public List<Node> getContent() {
        return content == null ? Collections.emptyList() : content;
    }
}
