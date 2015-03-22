package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

/**
 * Specification for a MessageSerializer, which should take a message type and return a netty ByteBuf object.
 */
public interface MessageSerializer {

    /**
     * Encode a message and return a ByteBuf instance with its contents.
     *
     * @param message The message type this MessageSerializer was created for.
     * @return The encoded message.
     */
    ByteBuf serialize(Object message);
}
