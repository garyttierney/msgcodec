package org.apollo.extension.releasegen.message.property;

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

    public PropertyType getElementType() {
        return elementType;
    }

    @Override
    public Class<?> getType() throws ClassNotFoundException {
        return Array.newInstance(elementType.getType(), 1).getClass();
    }
}
