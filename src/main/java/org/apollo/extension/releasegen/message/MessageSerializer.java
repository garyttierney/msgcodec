package org.apollo.extension.releasegen.message;

import io.netty.buffer.ByteBuf;

public interface MessageSerializer {
    ByteBuf serialize(Object message);
}
