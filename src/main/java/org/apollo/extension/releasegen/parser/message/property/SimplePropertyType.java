package org.apollo.extension.releasegen.parser.message.property;

public class SimplePropertyType implements PropertyType {
    private final Class<?> type;

    public SimplePropertyType(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
