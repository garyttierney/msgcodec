package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;

import java.util.Map;

public class MessageCodec {

    private final int version;

    private final Map<Integer, MessageDeserializer> deserializerMap;
    private final Map<Class<?>, MessageSerializer> serializerMap;

    public MessageCodec(int version, Map<Integer, MessageDeserializer> deserializerMap, Map<Class<?>, MessageSerializer> serializerMap) {
        this.version = version;
        this.deserializerMap = deserializerMap;
        this.serializerMap = serializerMap;
    }
}
