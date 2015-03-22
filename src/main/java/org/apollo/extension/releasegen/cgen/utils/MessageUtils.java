package org.apollo.extension.releasegen.cgen.utils;

import org.apollo.extension.releasegen.message.property.IntegerPropertyType;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

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


    public static PropertyDescriptor getPropertyDescriptor(BeanInfo info, String propertyName) {
        for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName)) {
                return descriptor;
            }
        }

        return null;
    }
}
