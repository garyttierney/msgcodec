package sfix.msgcodec.message.node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MessageNode {
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
     * Accept a {@link MessageNodeVisitor} and iterate call the {@link MessageNodeVisitor#visit} methods on this object and all of its properties.
     *
     * @param visitor The MessageNodeVisitor to call.
     */
    public void accept(MessageNodeVisitor visitor) throws MessageNodeVisitorException {
        visitor.visit(this);

        for (PropertyNode property : propertyList) {
            visitor.visitPropertyNode(property);
        }

        visitor.visitEnd(this);
    }

    /**
     * Return the list of property nodes from this message.
     *
     * @return An ordered list of property nodes.
     */
    public LinkedList<PropertyNode> getPropertyList() {
        return propertyList;
    }

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

    /**
     * Parser action to add a new {@link PropertyNode} to the property list.
     *
     * @param propertyNode The property node to add.
     */
    public boolean addProperty(PropertyNode propertyNode) {
        propertyList.addLast(propertyNode);
        return true;
    }


    /**
     * Parser action to add a new {@link AttributeNode} to the attribute map.
     *
     * @param attributeNode The attribute node to add.
     */
    public boolean addAttribute(AttributeNode attributeNode) {
        this.attributeNodeMap.put(attributeNode.getIdentifier(), attributeNode);
        return true;
    }

    /**
     * Checks if a attribute with the specified identifier is available.
     *
     * @param identifier The identifier.
     * @return True if the attribute exists, false otherwise.
     */
    public boolean hasAttribute(String identifier) {
        return attributeNodeMap.containsKey(identifier);
    }

    /**
     * Returns the {@link AttributeNode} with the specified identifier.
     *
     * @param identifier The identifier.
     * @return The attribute node.
     */
    public AttributeNode getAttribute(String identifier) {
        return attributeNodeMap.get(identifier);
    }
}
