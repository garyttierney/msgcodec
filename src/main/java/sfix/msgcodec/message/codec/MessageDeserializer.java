package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

/**
 * Specification for a MessageDeserializer, takes a ByteBuf as input and returns the deserialized message object.
 */
public interface MessageDeserializer {
    /**
     * Deserialize a ByteBuf input into a message and return it.
     *
     * @param input The input buffer.
     * @return The deserialized message.
     */
    Object deserialize(ByteBuf input);
}
