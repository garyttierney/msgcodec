package sfix.msgcodec.io;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

/**
 * @author Graham <grahamedgecombe.com>
 */
public class PacketReader {
    private final ByteBuf buffer;

    public PacketReader(ByteBuf buffer) {
        this.buffer = buffer;
    }

    /**
     * Reads a standard data type from the buffer with the specified order and transformation.
     *
     * @param type The data type.
     * @param order The data order.
     * @param transformation The data transformation.
     * @return The value.
     * @throws IllegalStateException If this reader is not in byte access mode.
     * @throws IllegalArgumentException If the combination is invalid.
     */
    public long get(DataType type, DataOrder order, DataTransformation transformation) {
        long longValue = 0;
        int length = type.getBytes();
        if (order == DataOrder.BIG) {
            for (int i = length - 1; i >= 0; i--) {
                if (i == 0 && transformation != DataTransformation.NONE) {
                    if (transformation == DataTransformation.ADD) {
                        longValue |= buffer.readByte() - 128 & 0xFFL;
                    } else if (transformation == DataTransformation.NEGATE) {
                        longValue |= -buffer.readByte() & 0xFFL;
                    } else if (transformation == DataTransformation.SUBTRACT) {
                        longValue |= 128 - buffer.readByte() & 0xFFL;
                    } else {
                        throw new IllegalArgumentException("Unknown transformation.");
                    }
                } else {
                    longValue |= (buffer.readByte() & 0xFFL) << i * 8;
                }
            }
        } else if (order == DataOrder.LITTLE) {
            for (int i = 0; i < length; i++) {
                if (i == 0 && transformation != DataTransformation.NONE) {
                    if (transformation == DataTransformation.ADD) {
                        longValue |= buffer.readByte() - 128 & 0xFFL;
                    } else if (transformation == DataTransformation.NEGATE) {
                        longValue |= -buffer.readByte() & 0xFFL;
                    } else if (transformation == DataTransformation.SUBTRACT) {
                        longValue |= 128 - buffer.readByte() & 0xFFL;
                    } else {
                        throw new IllegalArgumentException("Unknown transformation.");
                    }
                } else {
                    longValue |= (buffer.readByte() & 0xFFL) << i * 8;
                }
            }
        } else if (order == DataOrder.MIDDLE) {
            if (transformation != DataTransformation.NONE) {
                throw new IllegalArgumentException("Middle endian cannot be transformed.");
            }
            if (type != DataType.INT) {
                throw new IllegalArgumentException("Middle endian can only be used with an integer.");
            }
            longValue |= (buffer.readByte() & 0xFF) << 8;
            longValue |= buffer.readByte() & 0xFF;
            longValue |= (buffer.readByte() & 0xFF) << 24;
            longValue |= (buffer.readByte() & 0xFF) << 16;
        } else if (order == DataOrder.INVERSED_MIDDLE) {
            if (transformation != DataTransformation.NONE) {
                throw new IllegalArgumentException("Inversed middle endian cannot be transformed.");
            }
            if (type != DataType.INT) {
                throw new IllegalArgumentException("Inversed middle endian can only be used with an integer.");
            }
            longValue |= (buffer.readByte() & 0xFF) << 16;
            longValue |= (buffer.readByte() & 0xFF) << 24;
            longValue |= buffer.readByte() & 0xFF;
            longValue |= (buffer.readByte() & 0xFF) << 8;
        } else {
            throw new IllegalArgumentException("Unknown order.");
        }
        return longValue;
    }

    /**
     * Gets an unsigned data type from the buffer with the specified order and transformation.
     *
     * @param type The data type.
     * @param order The byte order.
     * @param transformation The data transformation.
     * @return The value.
     * @throws IllegalStateException If this reader is not in byte access mode.
     * @throws IllegalArgumentException If the combination is invalid.
     */
    public long getUnsigned(DataType type, DataOrder order, DataTransformation transformation) {
        long longValue = get(type, order, transformation);
        Preconditions.checkArgument(type != DataType.LONG, "Longs must be read as a signed type.");
        return longValue & 0xFFFFFFFFFFFFFFFFL;
    }

    /**
     * Reads a string from the specified {@link ByteBuf}.
     *
     * @param buffer The buffer.
     * @return The string.
     */
    public String getString() {
        StringBuilder builder = new StringBuilder();
        int character;
        while (buffer.isReadable() && (character = buffer.readUnsignedByte()) != DataConstants.STRING_TERMINATOR) {
            builder.append((char) character);
        }
        return builder.toString();
    }
}
