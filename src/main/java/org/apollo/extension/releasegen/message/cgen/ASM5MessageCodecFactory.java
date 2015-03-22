package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.message.MessageCodecFactory;
import org.apollo.extension.releasegen.message.MessageCodecFactoryException;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.MessageNodeVisitorException;
import org.objectweb.asm.ClassWriter;

/**
 * A bytecode generation based {@link MessageCodecFactory}.
 */
public class ASM5MessageCodecFactory implements MessageCodecFactory {
    private CGenClassLoader classLoader = new CGenClassLoader(ASM5MessageCodecFactory.class.getClassLoader());

    @Override
    public MessageDeserializer createDeserializer(MessageNode node) throws MessageCodecFactoryException
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String deserializerClassName = node.getIdentifier() + "Deserializer";
        try {
            node.accept(new MessageDeserializerClassWriter(deserializerClassName, cw));
        } catch (MessageNodeVisitorException e) {
            throw new MessageCodecFactoryException("Error occurred when generating class for deserializer \"" + deserializerClassName + "\"", e);
        }

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();

        Class<?> clazz = classLoader.defineClassProxy(deserializerClassName, classBytes, 0, classBytes.length);
        try {
            return (MessageDeserializer) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MessageCodecFactoryException("Unable to instantiate \"" + deserializerClassName + "\"", e);
        }
    }

    @Override
    public MessageSerializer createSerializer(MessageNode messageNode) throws MessageCodecFactoryException {
        return null;
    }
}
