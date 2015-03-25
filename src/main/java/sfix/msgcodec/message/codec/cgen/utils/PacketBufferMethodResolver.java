package sfix.msgcodec.message.codec.cgen.utils;

import sfix.msgcodec.io.*;
import sfix.msgcodec.message.property.IntegerPropertyType;
import sfix.msgcodec.message.property.PropertyType;

public class PacketBufferMethodResolver {
    private static final Class<PacketReader> READER_CLASS = PacketReader.class;
    private static final Class<PacketBuilder> BUILDER_CLASS = PacketBuilder.class;

    public static MethodReference getReadMethod(PropertyType type) throws NoSuchMethodException, ClassNotFoundException {
        if (type instanceof IntegerPropertyType) {
            if (((IntegerPropertyType) type).isSigned()) {
                return new MethodReference(READER_CLASS, READER_CLASS.getDeclaredMethod("get", DataType.class, DataOrder.class, DataTransformation.class));
            } else {
                return new MethodReference(READER_CLASS, READER_CLASS.getDeclaredMethod("getUnsigned", DataType.class, DataOrder.class, DataTransformation.class));
            }
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
            if(((IntegerPropertyType) type).isSigned()) {
                return new MethodReference(
                    BUILDER_CLASS,
                    BUILDER_CLASS.getDeclaredMethod("put", DataType.class, DataOrder.class, DataTransformation.class, Number.class)
                );
            } else {
                return new MethodReference(
                    BUILDER_CLASS,
                    BUILDER_CLASS.getDeclaredMethod("putUnsigned", DataType.class, DataOrder.class, DataTransformation.class, Number.class)
                );
            }
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
