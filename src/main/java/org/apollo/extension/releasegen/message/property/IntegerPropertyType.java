package org.apollo.extension.releasegen.message.property;

import org.apollo.extension.releasegen.io.DataOrder;

public class IntegerPropertyType implements PropertyType {

    /**
     * The bit width of this property.
     */
    private int bits;

    /**
     * Whether or not this value should be read as a signed or unsigned value.
     */
    private boolean signed = true;

    /**
     * The ordering of this Integer property, defaults to big endian.
     */
    private DataOrder dataOrder = DataOrder.BIG;

    public boolean isSigned() {
        return signed;
    }

    public boolean setSigned(boolean signed) {
        this.signed = signed;
        return true;
    }

    public int getBits() {
        return bits;
    }

    public boolean setBits(int bits) {
        this.bits = bits;
        return true;
    }

    @Override
    public Class<?> getType() {
        int numBits = signed ? bits : bits * 2; // up cast for unsigned types

        switch (numBits) {
            case 8:
                return byte.class;
            case 16:
                return short.class;
            case 32:
                return int.class;
            case 64:
                return long.class;
        }

        return long.class;
    }

    public boolean setDataOrder(DataOrder dataOrder) {
        this.dataOrder = dataOrder;
        return true;
    }

    public DataOrder getDataOrder() {
        return dataOrder;
    }
}
