package org.apollo.extension.releasegen.parser.message.node;

import java.util.HashMap;
import java.util.Map;

public class MessageNode {
    /**
     * The Message class this MessageNode represents.
     */
    private String messageIdentifier;

    /**
     * A mapping of attribute identifiers to {@link AttributeNode} instances.
     */
    private Map<String, AttributeNode> attributeNodeMap = new HashMap<>();


    /**
     * @return The Message class represented by this MessageNode.
     */
    public String getIdentifier() {
        return messageIdentifier;
    }

    /**
     * Parser action to set the Message class which is represented by this MessageNode.
     *
     * @param messageIdentifier The Message class.
     */
    public boolean setIdentifier(String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
        return true;
    }

    public boolean addAttribute(AttributeNode attributeNode) {
        this.attributeNodeMap.put(attributeNode.getIdentifier(), attributeNode);
        return true;
    }

    public boolean hasAttribute(String identifier) {
        return attributeNodeMap.containsKey(identifier);
    }

    public AttributeNode getAttribute(String identifier) {
        return attributeNodeMap.get(identifier);
    }
}
