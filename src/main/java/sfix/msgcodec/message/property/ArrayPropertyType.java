package sfix.msgcodec.message.property;

import java.lang.reflect.Array;

/**
 * A PropertyType implementation which defines an array wrapper of another PropertyType.
 */
public class ArrayPropertyType implements PropertyType {
    private PropertyType elementType;
    private String lengthSpecifier;

    /**
     * Create a new definition of an Array Property, which encapsulates an array of <code>elementType</code>.
     *
     * @param elementType The type of elements in this array.
     * @param lengthSpecifier The length specifier, which can be an integer constant or a message property identifier, resolved at generation time.
     */
    public ArrayPropertyType(PropertyType elementType, String lengthSpecifier) {
        this.elementType = elementType;
        this.lengthSpecifier = lengthSpecifier;
    }


    /**
     * The length this ArrayPropertyType was created with, which is resolved at
     * Serializer creation time. Can be an identifier or a constant.
     *
     * @return The length specifier.
     */
    public String getLengthSpecifier() {
        return lengthSpecifier;
    }

    /**
     * The PropertyType this array uses for its elements.
     *
     * @return The PropertyType of the elements of this array.
     */
    public PropertyType getElementType() {
        return elementType;
    }

    @Override
    public Class<?> getType() throws ClassNotFoundException {
        return Array.newInstance(elementType.getType(), 1).getClass();
    }
}
