package sfix.msgcodec.message.codec;

import sfix.msgcodec.message.node.MessageNode;

/**
 * A message discriminator implementation which differentiates messages simply based on their class.
 */
public class MessageClassDiscriminatorStrategy implements MessageDiscriminatorStrategy<Class<?>> {

    @Override
    public Class<?> getDiscriminator(MessageNode node) throws MessageCodecGeneratorException {
        try {
            return Class.forName(node.getIdentifier());
        } catch (ClassNotFoundException e) {
            throw new MessageCodecGeneratorException("Error! Unable to lookup message node identifier class", e);
        }
    }
}
