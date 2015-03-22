package org.apollo.extension.releasegen.message.node;

import org.apollo.extension.releasegen.message.property.PropertyType;

public class PropertyNode {
    private PropertyType type;
    private String identifier;

    public PropertyType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean setIdentifier(String identifier) {
        this.identifier = identifier;
        return true;

    }

    public boolean setType(PropertyType type) {
        this.type = type;
        return true;
    }

}
