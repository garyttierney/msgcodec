package org.apollo.extension.releasegen.message;

import java.nio.ByteBuffer;

public interface MessageDeserializer {
    Object deserialize(ByteBuffer buffer);
}
