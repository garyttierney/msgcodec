package sfix.msgcodec.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Graham <grahamedgecombe.com>
 */
public class PacketBuilder {

    private ByteBuf buffer = Unpooled.buffer();

    /**
     * Puts a standard data type with the specified value, byte order and transformation.
     *
     * @param type The data type.
     * @param order The byte order.
     * @param transformation The transformation.
     * @param value The value.
     * @throws IllegalArgumentException If the type, order, or transformation is unknown.
     */
    public void put(DataType type, DataOrder order, DataTransformation transformation, Number value) {
        long longValue = value.longValue();
        int length = type.getBytes();
        if (order == DataOrder.BIG) {
            for (int i = length - 1; i >= 0; i--) {
                if (i == 0 && transformation != DataTransformation.NONE) {
                    if (transformation == DataTransformation.ADD) {
                        buffer.writeByte((byte) (longValue + 128));
                    } else if (transformation == DataTransformation.NEGATE) {
                        buffer.writeByte((byte) -longValue);
                    } else if (transformation == DataTransformation.SUBTRACT) {
                        buffer.writeByte((byte) (128 - longValue));
                    } else {
                        throw new IllegalArgumentException("Unknown transformation.");
                    }
                } else {
                    buffer.writeByte((byte) (longValue >> i * 8));
                }
            }
        } else if (order == DataOrder.LITTLE) {
            for (int i = 0; i < length; i++) {
                if (i == 0 && transformation != DataTransformation.NONE) {
                    if (transformation == DataTransformation.ADD) {
                        buffer.writeByte((byte) (longValue + 128));
                    } else if (transformation == DataTransformation.NEGATE) {
                        buffer.writeByte((byte) -longValue);
                    } else if (transformation == DataTransformation.SUBTRACT) {
                        buffer.writeByte((byte) (128 - longValue));
                    } else {
                        throw new IllegalArgumentException("Unknown transformation.");
                    }
                } else {
                    buffer.writeByte((byte) (longValue >> i * 8));
                }
            }
        } else if (order == DataOrder.MIDDLE) {
            if (transformation != DataTransformation.NONE) {
                throw new IllegalArgumentException("Middle endian cannot be transformed.");
            }
            if (type != DataType.INT) {
                throw new IllegalArgumentException("Middle endian can only be used with an integer,");
            }
            buffer.writeByte((byte) (longValue >> 8));
            buffer.writeByte((byte) longValue);
            buffer.writeByte((byte) (longValue >> 24));
            buffer.writeByte((byte) (longValue >> 16));
        } else if (order == DataOrder.INVERSED_MIDDLE) {
            if (transformation != DataTransformation.NONE) {
                throw new IllegalArgumentException("Inversed middle endian cannot be transformed,");
            }
            if (type != DataType.INT) {
                throw new IllegalArgumentException("Inversed middle endian can only be used with an integer,");
            }
            buffer.writeByte((byte) (longValue >> 16));
            buffer.writeByte((byte) (longValue >> 24));
            buffer.writeByte((byte) longValue);
            buffer.writeByte((byte) (longValue >> 8));
        } else {
            throw new IllegalArgumentException("Unknown order.");
        }
    }
}
