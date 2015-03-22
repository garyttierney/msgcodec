package org.apollo.extension.releasegen.message.node;

import org.apollo.extension.releasegen.message.property.PropertyType;

public class PropertyNode {
    private PropertyType type;
    private String identifier;
    private CompoundPropertyNode parent;


    public PropertyType getType() {
        return type;
    }

    public String getIdentifier() {
        return hasParent() ? parent.getIdentifier() + "$" + identifier : identifier;
    }

    public boolean setIdentifier(String identifier) {
        this.identifier = identifier;
        return true;

    }

    public boolean setParent(CompoundPropertyNode parent) {
        this.parent = parent;
        return true;
    }

    public boolean setType(PropertyType type) {
        this.type = type;
        return true;
    }

    public boolean hasParent() {
        return parent != null;
    }
}
