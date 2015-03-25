package sfix.msgcodec.message.parser;

import sfix.msgcodec.message.node.AttributeNode;
import sfix.msgcodec.message.node.AttributeType;
import sfix.msgcodec.message.node.MessageNode;

import java.util.*;

public class YamlMessageParser {

    /**
     * A list of property names to ignore when parsing the MessageNode attributeList.
     */
    private static final List<String> RESERVED_PROPERTIES = Collections.unmodifiableList(Arrays.asList("properties"));

    public MessageNode parseYamlObject(Map<String, Object> yamlObject) {
        MessageNode messageNode = new MessageNode();

        messageNode.setAttributeList(parseYamlAttributeList(yamlObject));
        return null;
    }

    public List<AttributeNode> parseYamlAttributeList(Map<String, Object> yamlObject) {
        List<AttributeNode> attributeNodes = new ArrayList<>();
        for(Map.Entry<String, Object> entry : yamlObject.entrySet()) {
            if(RESERVED_PROPERTIES.contains(entry.getKey())) {
                continue;
            }

            AttributeNode attributeNode = new AttributeNode();
            attributeNode.setIdentifier(entry.getKey());
            attributeNode.setValue((String) entry.getValue());
            if (attributeNode.getValue().indexOf('@') == 1) {
                attributeNode.setType(AttributeType.REFERENCE);
                attributeNode.setValue(attributeNode.getIdentifier().substring(1));
            } else if (attributeNode.getValue().matches("[0-9]+")) {
                attributeNode.setType(AttributeType.NUMBER_LITERAL);
            } else {
                attributeNode.setType(AttributeType.STRING_LITERAL);
            }

            attributeNodes.add(attributeNode);
         }

        return attributeNodes;
    }
}
