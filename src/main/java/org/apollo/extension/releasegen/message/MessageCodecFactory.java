package org.apollo.extension.releasegen.message;

import org.apollo.extension.releasegen.message.node.MessageNode;

/**
 * A specification for a MessageCodecFactory which allows the generation of the {@link MessageDeserializer} and {@link MessageSerializer} classes to be configured.
 *
 * @param <D> The data class used for storing serialized messages, used for writing messages and reading them.
 */
public interface MessageCodecFactory {
    /**
     * From a given {@link MessageNode} specification, create a MessageDeserializer that takes the type parameter <code>D</code> as input.
     *
     * @param messageNode The specification to create the deserializer from.
     * @return A new deserializer instance which decodes a Message from an input <code>D</code>.
     */
    MessageDeserializer createDeserializer(MessageNode messageNode) throws MessageCodecFactoryException;

    /**
     * From a given {@link MessageNode} specification, create a MessageSerializer that takes a message as input, and writes it to an output type <code>D</code>.
     *
     * @param messageNode The specification to create the serializer from.
     * @return A new serializer instance which encodes a message to an output <code>D</code>.
     */
    MessageSerializer createSerializer(MessageNode messageNode) throws MessageCodecFactoryException;
}
