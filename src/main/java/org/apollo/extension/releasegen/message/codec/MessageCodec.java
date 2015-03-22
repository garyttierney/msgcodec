package org.apollo.extension.releasegen.message.codec;

import io.netty.buffer.ByteBuf;

import java.util.Map;

public class MessageCodec {

    private final Map<Integer, MessageDeserializer> deserializerMap;
    private final Map<Class<?>, MessageSerializer> serializerMap;

    public MessageCodec(Map<Integer, MessageDeserializer> deserializerMap, Map<Class<?>, MessageSerializer> serializerMap) {
        this.deserializerMap = deserializerMap;
        this.serializerMap = serializerMap;
    }

    public ByteBuf serialize(Object message) {
        return serializerMap.get(message.getClass()).serialize(message);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(int opcode, ByteBuf buffer) {
        return (T) deserializerMap.get(opcode).deserialize(buffer);
    }
}
