package entities.wikidata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class WikiDataSnakDeserializer extends JsonDeserializer<WikiDataSnakDatavalue> {
    private boolean checkType(TreeNode treeNode, String type) {
        if (treeNode.get("type") == null || !(treeNode.get("type") instanceof TextNode)) {
            return false;
        }
        TextNode textNode = (TextNode)treeNode.get("type");
        return type.equals(textNode.asText());
    }

    @Override
    public WikiDataSnakDatavalue deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final ObjectMapper mapper = (ObjectMapper)parser.getCodec();
        final TreeNode node = mapper.readTree(parser);
        if (checkType(node, "time")) {
            return mapper.treeToValue(node, WikiDataSnakDatavalueTime.class);
        } else if (checkType(node, "string")) {
            return mapper.treeToValue(node, WikiDataSnakDatavalueString.class);
        } else {
            //System.out.println("unknown node of type " + node.get("type"));
            return mapper.treeToValue(node, WikiDataSnakDatavalueUnknown.class);
        }
    }
}
