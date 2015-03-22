package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.MessageNodeVisitor;
import org.apollo.extension.releasegen.message.node.MessageNodeVisitorException;
import org.apollo.extension.releasegen.message.node.PropertyNode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.objectweb.asm.Opcodes.*;


public class MessageDeserializerClassWriter implements MessageNodeVisitor {
    private static final Class<MessageDeserializer> deserializerInterface = MessageDeserializer.class;

    private final ClassVisitor cv;
    private final String className;
    private final MethodReferenceResolver methodReferenceResolver;

    private MessageDeserializerMethodWriter deserializeMethodWriter;

    public MessageDeserializerClassWriter(String className, ClassVisitor classVisitor, MethodReferenceResolver methodReferenceResolver) {
        this.cv = classVisitor;
        this.className = className;
        this.methodReferenceResolver = methodReferenceResolver;
    }

    @Override
    public void visit(MessageNode messageNode) throws MessageNodeVisitorException {
        String className = this.className.replace('.', '/');
        String interfaceClassName = Type.getInternalName(MessageDeserializer.class);

        cv.visit(V1_7,
            ACC_PUBLIC | ACC_SUPER,
            className, null, Type.getInternalName(Object.class), new String[] { interfaceClassName }
        );

        MethodVisitor mv;
        try {
            mv = cv.visitMethod(ACC_PUBLIC, "<init>", Type.getConstructorDescriptor(Object.class.getConstructor()), null, null);
        } catch (NoSuchMethodException e) {
            throw new MessageNodeVisitorException("Failed to get Object.<init> method", e);
        }

        Label startLabel = new Label();
        Label endLabel = new Label();

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0); // push `this` to the operand stack
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitLocalVariable("this", "L" + className + ";", null, startLabel, endLabel, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

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
            ),
            methodReferenceResolver
        );

        deserializeMethodWriter.visit(messageNode);
    }

    @Override
    public void visitPropertyNode(PropertyNode node) throws MessageNodeVisitorException {
        deserializeMethodWriter.visitPropertyNode(node);
    }

    @Override
    public void visitEnd(MessageNode node) throws MessageNodeVisitorException {
        deserializeMethodWriter.visitEnd(node);
        cv.visitEnd();
    }

}
