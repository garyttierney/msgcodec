package sfix.msgcodec.message.codec;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.Map;

/**
 * A codec implementation which handles serialization and deserialization of messages.
 *
 * @param <D> The discriminator type for messages handled by this codec.
 */
public final class MessageCodec<D> {

    /**
     * A map of discriminators to message deserializers.
     */
    private final Map<D, MessageDeserializer> deserializerMap;

    /**
     * A map of message object types to message serializers.
     */
    private final Map<Class<?>, MessageSerializer> serializerMap;

    public MessageCodec(Map<D, MessageDeserializer> deserializerMap, Map<Class<?>, MessageSerializer> serializerMap) {
        this.deserializerMap = Collections.unmodifiableMap(deserializerMap);
        this.serializerMap = Collections.unmodifiableMap(serializerMap);
    }

    /**
     * Lookup the message class in the {@link #serializerMap} and call its {@link MessageSerializer#serialize} method with the message parameter.
     *
     * @param message The message to serialize.
     * @return The serialized message as a ByteBuf.
     */
    public ByteBuf serialize(Object message) {
        return serializerMap.get(message.getClass()).serialize(message);
    }

    /**
     * Lookup the message discriminator in the {@link #deserializerMap} and call its {@link MessageDeserializer#deserialize} method with the message ByteBuf parameter.
     *
     * @param discriminator The discriminator to lookup.
     * @param buffer The input buffer.
     * @param <T> The type of message to deserialize.
     *
     * @return The deserialized message instance.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(D discriminator, ByteBuf buffer) {
        return (T) deserializerMap.get(discriminator).deserialize(buffer);
    }
}
