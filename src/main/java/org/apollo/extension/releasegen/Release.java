package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;

import java.util.Map;

public class Release {

    private final int version;
    private final Map<Integer, MessageDeserializer> deserializerMap;
    private final Map<Integer, MessageSerializer> serializerMap;

    public Release(int version, Map<Integer, MessageDeserializer> deserializerMap, Map<Integer, MessageSerializer> serializerMap) {
        this.version = version;
        this.deserializerMap = deserializerMap;
        this.serializerMap = serializerMap;
    }
}
