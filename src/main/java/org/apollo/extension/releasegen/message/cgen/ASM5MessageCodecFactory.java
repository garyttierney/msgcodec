package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.message.MessageCodecFactory;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;
import org.apollo.extension.releasegen.message.node.MessageNode;

/**
 *
 * @param <D> The da
 */
public class ASM5MessageCodecFactory<D> implements MessageCodecFactory<D> {
    @Override
    public MessageDeserializer<D> createDeserializer(MessageNode messageNode) {
        return null;
    }

    @Override
    public MessageSerializer<D> createSerializer(MessageNode messageNode) {
        return null;
    }
}
