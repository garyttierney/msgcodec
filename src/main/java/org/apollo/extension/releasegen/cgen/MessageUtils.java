package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.property.IntegerPropertyType;

public class MessageUtils {
    public static String getByteBufferWriteMethod(IntegerPropertyType type) {
        switch(type.getBits()) {
            case 8:
                return "put";
            case 16:
                return "putShort";
            case 32:
                return "putInt";
            case 64:
                return "putLong";
            default:
                throw new IllegalStateException("Invalid number of bits in IntegerPropertyType");
        }
    }

    public static String getByteBufferReadMethod(IntegerPropertyType type) {
        return getByteBufferWriteMethod(type).replace("put", "get");
    }


}
