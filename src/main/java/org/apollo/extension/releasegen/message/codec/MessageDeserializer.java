package org.apollo.extension.releasegen.message.codec;

import io.netty.buffer.ByteBuf;

public interface MessageDeserializer {
    Object deserialize(ByteBuf input);
}
