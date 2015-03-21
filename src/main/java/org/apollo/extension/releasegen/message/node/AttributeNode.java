package org.apollo.extension.releasegen.message.node;

public class AttributeNode {

    private String identifier;
    private String value;
    private AttributeType type;

    public String getIdentifier() {
        return identifier;
    }

    public boolean setIdentifier(String identifier) {
        this.identifier = identifier;
        return true;
    }

    public String getValue() {
        return value;
    }

    public boolean setValue(String value) {
        this.value = value;
        return true;
    }

    public AttributeType getType() {
        return type;
    }

    public boolean setType(AttributeType type) {
        this.type = type;
        return true;
    }

}
