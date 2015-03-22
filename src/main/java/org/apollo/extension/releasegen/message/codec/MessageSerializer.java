package org.apollo.extension.releasegen.message.codec;

import io.netty.buffer.ByteBuf;

public interface MessageSerializer {
    ByteBuf serialize(Object message);
}
