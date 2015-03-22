package org.apollo.extension.releasegen.message.node;

import java.util.HashMap;
import java.util.Map;

public class CompoundPropertyNode extends PropertyNode {
    private final Map<String, PropertyNode> children = new HashMap<>();

    /**
     * Parser action which adds a child to this property and
     * @param node
     * @return
     */
    public boolean addChild(PropertyNode node) {
        children.put(node.getIdentifier(), node);
        return node.setParent(this);
    }

    public boolean hasChild(String identifier) {
        return children.containsKey(identifier);
    }
}
