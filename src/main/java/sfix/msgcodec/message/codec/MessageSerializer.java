package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

public interface MessageSerializer {
    ByteBuf serialize(Object message);
}
