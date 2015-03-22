package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

public interface MessageDeserializer {
    Object deserialize(ByteBuf input);
}
