package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.io.*;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.apollo.extension.releasegen.message.property.PropertyType;

public class PacketMethodReferenceResolver {
    private static final Class<PacketReader> READER_CLASS = PacketReader.class;
    private static final Class<PacketBuilder> BUILDER_CLASS = PacketBuilder.class;
    
    public static MethodReference getReadMethod(PropertyType type) throws NoSuchMethodException, ClassNotFoundException {
        if (type instanceof IntegerPropertyType) {
            return new MethodReference(READER_CLASS, READER_CLASS.getDeclaredMethod("get", DataType.class, DataOrder.class, DataTransformation.class));
        } else {
            Class<?> dataType = type.getType();
            if (dataType == String.class) {
                return new MethodReference(READER_CLASS, READER_CLASS.getDeclaredMethod("getString"));
            } else {
                throw new UnsupportedOperationException("We don't know how to decode a \"" + dataType.getName() + "\"");
            }
        }
    }

    public static MethodReference getWriteMethod(PropertyType type) throws NoSuchMethodException, ClassNotFoundException {
        if (type instanceof IntegerPropertyType) {
            return new MethodReference(
                BUILDER_CLASS,
                BUILDER_CLASS.getDeclaredMethod("put", DataType.class, DataOrder.class,DataTransformation.class, Number.class)
            );
        } else {
            Class<?> dataType = type.getType();
            if (dataType == String.class) {
                return new MethodReference(BUILDER_CLASS, BUILDER_CLASS.getDeclaredMethod("putString", String.class));
            } else {
                throw new UnsupportedOperationException("We don't know how to encode a \"" + dataType.getName() + "\"");
            }
        }
    }
}
