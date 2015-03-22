package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * A codec implementation which handles serialization and deserialization of messages.
 *
 * @param <D> The discriminator type for messages handled by this codec.
 */
public class MessageCodec<D> {

    private final Map<D, MessageDeserializer> deserializerMap;
    private final Map<Class<?>, MessageSerializer> serializerMap;

    public MessageCodec(Map<D, MessageDeserializer> deserializerMap, Map<Class<?>, MessageSerializer> serializerMap) {
        this.deserializerMap = deserializerMap;
        this.serializerMap = serializerMap;
    }

    public ByteBuf serialize(Object message) {
        return serializerMap.get(message.getClass()).serialize(message);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(D discriminator, ByteBuf buffer) {
        return (T) deserializerMap.get(discriminator).deserialize(buffer);
    }
}
