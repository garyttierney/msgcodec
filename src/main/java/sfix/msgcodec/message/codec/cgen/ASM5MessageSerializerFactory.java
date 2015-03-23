package sfix.msgcodec.message.codec.cgen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import sfix.msgcodec.message.codec.MessageDeserializer;
import sfix.msgcodec.message.codec.MessageSerializer;
import sfix.msgcodec.message.codec.MessageSerializerFactory;
import sfix.msgcodec.message.codec.MessageSerializerFactoryException;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.node.MessageNodeVisitorException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A bytecode generation based {@link sfix.msgcodec.message.codec.MessageSerializerFactory}.
 */
public class ASM5MessageSerializerFactory implements MessageSerializerFactory {
    private CGenClassLoader classLoader = new CGenClassLoader(ASM5MessageSerializerFactory.class.getClassLoader());

    @Override
    public MessageDeserializer createDeserializer(MessageNode node) throws MessageSerializerFactoryException
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String deserializerClassName = node.getIdentifier() + "Deserializer";
        try {
            node.accept(new MessageDeserializerClassWriter(deserializerClassName, cw));
        } catch (MessageNodeVisitorException e) {
            throw new MessageSerializerFactoryException("Error occurred when generating class for deserializer \"" + deserializerClassName + "\"", e);
        }

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        CheckClassAdapter.verify(new ClassReader(classBytes), false, pw);
        System.out.println(sw.toString());

        Class<?> clazz = classLoader.defineClassProxy(deserializerClassName, classBytes, 0, classBytes.length);
        try {
            return (MessageDeserializer) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MessageSerializerFactoryException("Unable to instantiate \"" + deserializerClassName + "\"", e);
        }
    }

    @Override
    public MessageSerializer createSerializer(MessageNode messageNode) throws MessageSerializerFactoryException {
        return null;
    }
}
