package org.apollo.extension.releasegen.message.property;

import org.apollo.extension.releasegen.io.DataOrder;
import org.apollo.extension.releasegen.io.DataTransformation;
import org.apollo.extension.releasegen.io.DataType;

public class IntegerPropertyType implements PropertyType {
    /**
     * Whether or not this value should be read as a signed or unsigned value.
     */
    private boolean signed = true;
    /**
     * The transformation applied to components of this integer type.
     */
    private DataTransformation dataTransformation = DataTransformation.NONE;
    /**
     * The integer type represented by this property.
     */
    private DataType dataType;
    /**
     * The ordering of this Integer property, defaults to big endian.
     */
    private DataOrder dataOrder = DataOrder.BIG;

    public DataTransformation getDataTransformation() {
        return dataTransformation;
    }

    public boolean setDataTransformation(DataTransformation dataTransformation) {
        this.dataTransformation = dataTransformation;
        return true;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean setSigned(boolean signed) {
        this.signed = signed;
        return true;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean setDataType(DataType dataType) {
        this.dataType = dataType;
        return true;
    }

    @Override
    public Class<?> getType() {
        int numBits = dataType.getBytes() * 8;

        if (numBits >= 1 && numBits <= 8) {
            return byte.class;
        } else if (numBits >= 9 && numBits <= 16) {
            return short.class;
        } else if (numBits >= 17 && numBits <= 32) {
            return int.class;
        } else if (numBits >= 33 && numBits <= 64) {
            return long.class;
        } else {
            throw new IllegalStateException("Tried to create an integer type with an invalid size");
        }
    }

    public boolean setDataOrder(DataOrder dataOrder) {
        this.dataOrder = dataOrder;
        return true;
    }

    public DataOrder getDataOrder() {
        return dataOrder;
    }
}
