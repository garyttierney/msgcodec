package org.apollo.extension.releasegen.parser.message.property;

import java.lang.reflect.Array;

public class ArrayPropertyType implements PropertyType{
    private PropertyType elementType;
    private String lengthSpecifier;

    public boolean setLengthSpecifier(String lengthSpecifier) {
        this.lengthSpecifier = lengthSpecifier;
        return true;
    }

    public String getLengthSpecifier() {
        return lengthSpecifier;
    }

    public boolean setPropertyType(PropertyType elementType) {
        this.elementType = elementType;
        return true;
    }

    public ArrayPropertyType(PropertyType elementType, String lengthSpecifier) {
        this.elementType = elementType;
        this.lengthSpecifier = lengthSpecifier;
    }

    @Override
    public Class<?> getType() {
        return Array.newInstance(elementType.getType()).getClass();
    }
}
