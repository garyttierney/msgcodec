package sfix.msgcodec.message.node;

import java.util.Collection;
import java.util.LinkedList;

public class CompoundPropertyNode extends PropertyNode {
    private final LinkedList<PropertyNode> children = new LinkedList<>();

    /**
     * Parser action which adds a child to this property and
     */
    public boolean addChild(PropertyNode node) {
        children.addLast(node);
        return true;
    }

    public boolean hasChild(String identifier) {
        for (PropertyNode node : children) {
            if (identifier.equals(node.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    public Collection<PropertyNode> getChildren() {
        return children;
    }
}
