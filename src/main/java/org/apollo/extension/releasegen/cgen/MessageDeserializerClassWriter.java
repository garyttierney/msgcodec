package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.node.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.objectweb.asm.Opcodes.*;


public class MessageDeserializerClassWriter implements MessageNodeVisitor {
    private static final Class<MessageDeserializer> deserializerInterface = MessageDeserializer.class;

    private final ClassVisitor cv;
    private final String className;

    private MessageDeserializerMethodWriter deserializeMethodWriter;

    public MessageDeserializerClassWriter(String className, ClassVisitor classVisitor) {
        this.cv = classVisitor;
        this.className = className;
    }

    @Override
    public void visit(MessageNode messageNode) throws MessageNodeVisitorException {
        String className = this.className.replace('.', '/');
        String interfaceClassName = Type.getInternalName(MessageDeserializer.class);

        cv.visit(V1_7,
                ACC_PUBLIC | ACC_SUPER,
                className, null, Type.getInternalName(Object.class), new String[] { interfaceClassName }
        );

        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

        mv.visitMaxs(1, 1);
        mv.visitVarInsn(ALOAD, 0); // push `this` to the operand stack
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);

        Method deserializeMethod;
        try {
            deserializeMethod = deserializerInterface.getMethod("deserialize", ByteBuffer.class);
        } catch (NoSuchMethodException e) {
            throw new MessageNodeVisitorException("Unable to find deserialize method in MessageDeserializer interface!", e);
        }

        deserializeMethodWriter = new MessageDeserializerMethodWriter(
            cv.visitMethod(
                ACC_PUBLIC,
                deserializeMethod.getName(),
                Type.getMethodDescriptor(deserializeMethod),
                null,
                null
            )
        );

        deserializeMethodWriter.visit(messageNode);
    }

    @Override
    public void visitCompoundProperty(CompoundPropertyNode node) throws MessageNodeVisitorException {
        deserializeMethodWriter.visitCompoundProperty(node);
    }

    @Override
    public void visitPropertyNode(PropertyNode node) throws MessageNodeVisitorException {
        deserializeMethodWriter.visitPropertyNode(node);
    }

    @Override
    public void visitEnd(MessageNode node) throws MessageNodeVisitorException {
        deserializeMethodWriter.visitEnd(node);
    }

}
