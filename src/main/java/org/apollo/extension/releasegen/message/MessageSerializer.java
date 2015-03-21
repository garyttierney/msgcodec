package org.apollo.extension.releasegen.message;

import java.nio.ByteBuffer;

public interface MessageSerializer {
    void serialize(ByteBuffer out, Object message);
}
