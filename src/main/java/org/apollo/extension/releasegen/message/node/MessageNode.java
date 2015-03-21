package org.apollo.extension.releasegen.message.node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MessageNode {
    /**
     * Accept a {@link MessageNodeVisitor} and iterate call the {@link MessageNodeVisitor#visit} methods on this object and all of its properties.
     *
     * @param visitor The MessageNodeVisitor to call.
     */
    public void accept(MessageNodeVisitor visitor) {
        visitor.visit(this);

        for(PropertyNode property : propertyList) {
            if(property instanceof CompoundPropertyNode) {
                visitor.visitCompoundProperty((CompoundPropertyNode) property);
            } else {
                visitor.visitPropertyNode(property);
            }
        }

        visitor.visitEnd(this);
    }

    /**
     * An ordered list of property nodes.
     */
    private final LinkedList<PropertyNode> propertyList = new LinkedList<>();

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

    public boolean addProperty(PropertyNode propertyNode) {
        propertyList.addLast(propertyNode);
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
