package sfix.msgcodec.message.node;

import java.util.*;

public class MessageNode {
    /**
     * The AttributeNode list for this MessageNode.
     */
    private final List<AttributeNode> attributeList = new ArrayList<>();

    /**
     * An ordered list of property nodes.
     */
    private final LinkedList<PropertyNode> propertyList = new LinkedList<>();

    /**
     * The Message class this MessageNode represents.
     */
    private String messageIdentifier;

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
     * Checks if a attribute with the specified identifier is available.
     *
     * @param identifier The identifier.
     * @return True if the attribute exists, false otherwise.
     */
    public boolean hasAttribute(String identifier) {
        for(AttributeNode node : attributeList) {
            if(node.getIdentifier().equals(identifier)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the {@link AttributeNode} with the specified identifier.
     *
     * @param identifier The identifier.
     * @return The attribute node.
     */
    public AttributeNode getAttribute(String identifier) {
        for(AttributeNode node : attributeList) {
            if(node.getIdentifier().equals(identifier)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Parser action to set this <code>MessageNode</code>s attribute list.
     *
     * @param attributeList The list of <code>AttributeNode</code>s to add.
     */
    public boolean setAttributeList(List<AttributeNode> attributeList) {
        this.attributeList.addAll(attributeList);
        return true;
    }

    public List<AttributeNode> getAttributeList() {
        return Collections.unmodifiableList(attributeList);
    }
}
