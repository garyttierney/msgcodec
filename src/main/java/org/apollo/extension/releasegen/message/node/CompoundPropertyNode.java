package org.apollo.extension.releasegen.message.node;

import java.util.HashMap;
import java.util.Map;

public class CompoundPropertyNode extends PropertyNode {
    private final Map<String, PropertyNode> children = new HashMap<>();

    public boolean addChild(PropertyNode node) {
        children.put(node.getIdentifier(), node);
        return true;
    }

    public boolean hasChild(String identifier) {
        return children.containsKey(identifier);
    }
}
