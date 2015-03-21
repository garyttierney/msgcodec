package org.apollo.extension.releasegen.parser.message.property;

public class IntegerPropertyType implements PropertyType {
    private int bits;
    private boolean signed = true;

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

        switch(numBits) {
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
}
