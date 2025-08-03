package utils;

import utils.markup.*;

import java.util.ArrayList;
import java.util.List;

public class MarkupParser {

    public static Doc parse(String text) {
        if (text == null) {
            text = "";
        }

        return new Doc(parse(new Slice(text)));
    }

    private final static List<Node> NODES = List.of(new Bold(), new Italic(), new Title(), new Newline(), new Pic(), new PicAuto(), new Url(), new Link(), new Size(), new Color());

    public static List<Node> parse(Slice slice) {
        List<Node> nodes = new ArrayList<>();

        Slice ts = slice;
        text : while (!ts.isEmpty()) {
            for (Node node : NODES) {
                int match = node.match(ts);
                if (match > 0) {
                    if (ts.length() != slice.length()) {
                        nodes.add(new Text(slice.sub(0, slice.length() - ts.length())));
                    }
                    nodes.add(node.get(ts.sub(0, match)));
                    slice = slice.sub(slice.length() - ts.length() + match);
                    ts = slice;
                    continue text;
                }
            }
            ts = ts.sub(1);
        }
        if (slice.length() > 0) {
            nodes.add(new Text(slice.sub(0, slice.length() - ts.length())));
        }

        return nodes;
    }
}
