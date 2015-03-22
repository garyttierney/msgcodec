package sfix.msgcodec.message.node;

import com.google.common.base.Objects;
import sfix.msgcodec.message.property.PropertyType;

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

    public boolean equals(Object other) {
        if (!(other instanceof PropertyNode)) {
            return false;
        }

        PropertyNode otherProperty = (PropertyNode) other;
        return otherProperty.identifier.equals(this.identifier) && otherProperty.type.equals(this.type);
    }

    public int hashCode() {
        return Objects.hashCode(identifier, type);
    }
}
