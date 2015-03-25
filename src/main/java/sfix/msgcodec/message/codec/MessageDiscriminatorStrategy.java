package sfix.msgcodec.message.codec;

import sfix.msgcodec.message.node.MessageNode;

/**
 * A specification class for a strategy on looking up message discriminators based on a {@link MessageNode}. The basic implementation uses
 * an opcode and direction discriminator, for an example of how to implement this class see {@link MessageClassDiscriminatorStrategy}.
 *
 * @param <D> The message discriminator type.
 */
public interface MessageDiscriminatorStrategy<D> {

    /**
     * For a {@link MessageNode} look up a discriminator and return it. <b>NOTE</b>: Types used as discriminators must correctly implement
     * equals(Object) and hashCode().
     *
     * @param node The node to lookup a discriminator for.
     * @return The discriminator for the specified message node.
     */
    D getDiscriminator(MessageNode node) throws MessageCodecGeneratorException;
}
