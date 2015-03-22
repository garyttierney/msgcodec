package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.apollo.extension.releasegen.message.property.PropertyType;

import java.nio.ByteBuffer;

public class ByteBufferMethodReferenceResolver implements MethodReferenceResolver {
    private static final Class<ByteBuffer> BYTE_BUFFER_CLASS = ByteBuffer.class;

    @Override
    public MethodReference getReadMethod(PropertyType type) throws ClassNotFoundException, NoSuchMethodException {
        if (type instanceof IntegerPropertyType) {
            IntegerPropertyType intType = (IntegerPropertyType) type;
            switch(intType.getBits()) {
                case 8:
                    return new MethodReference(BYTE_BUFFER_CLASS, BYTE_BUFFER_CLASS.getDeclaredMethod("get"));
                case 16:
                    return new MethodReference(BYTE_BUFFER_CLASS, BYTE_BUFFER_CLASS.getDeclaredMethod("getShort"));
                case 32:
                    return new MethodReference(BYTE_BUFFER_CLASS, BYTE_BUFFER_CLASS.getDeclaredMethod("getInt"));
                case 64:
                    return new MethodReference(BYTE_BUFFER_CLASS, BYTE_BUFFER_CLASS.getDeclaredMethod("getLong"));


            }
        }

        return null;
    }

    @Override
    public MethodReference getWriteMethod(PropertyType type) {
        return null;
    }
}
