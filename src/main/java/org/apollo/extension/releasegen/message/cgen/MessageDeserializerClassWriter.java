package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.io.GamePacketReaderFactory;
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

import static org.objectweb.asm.Opcodes.*;


public class MessageDeserializerClassWriter<I> implements MessageNodeVisitor {
    private static final Class<MessageDeserializer> deserializerInterface = MessageDeserializer.class;

    private final ClassVisitor cv;
    private final String className;
    private final MethodReferenceResolver methodReferenceResolver;
    private final Class<? extends GamePacketReaderFactory<I>> readerFactoryClass;
    private final Class<I> inputClass;

    private MessageDeserializerMethodWriter deserializeMethodWriter;

    public MessageDeserializerClassWriter(
        String className,
        ClassVisitor classVisitor,
        MethodReferenceResolver methodReferenceResolver,
        Class<? extends GamePacketReaderFactory<I>> readerFactoryClass,
        Class<I> inputClass
    ) {
        this.cv = classVisitor;
        this.className = className;
        this.methodReferenceResolver = methodReferenceResolver;
        this.readerFactoryClass = readerFactoryClass;
        this.inputClass = inputClass;
    }

    @Override
    public void visit(MessageNode messageNode) throws MessageNodeVisitorException {
        String className = this.className.replace('.', '/');
        String interfaceClassName = Type.getInternalName(MessageDeserializer.class);

        cv.visit(V1_7,
            ACC_PUBLIC | ACC_SUPER,
            className, null, Type.getInternalName(Object.class), new String[] { interfaceClassName }
        );

        cv.visitField(ACC_PRIVATE, "readerFactory", Type.getDescriptor(readerFactoryClass), null, null);

        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(readerFactoryClass)), null, null);
        Label startLabel = new Label();
        Label endLabel = new Label();

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0); // push `this` to the operand stack
        mv.visitMethodInsn( // call super constructor
            INVOKESPECIAL,
            Type.getInternalName(Object.class),
            "<init>",
            Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(readerFactoryClass)),
            false
        );

        mv.visitVarInsn(ALOAD, 0); // push `this` to the operand stack
        mv.visitVarInsn(ALOAD, 1); // push reader factory arg to the stack
        mv.visitFieldInsn(PUTFIELD, className, "readerFactory", Type.getDescriptor(readerFactoryClass));

        mv.visitInsn(RETURN);
        mv.visitLocalVariable("this", "L" + className + ";", null, startLabel, endLabel, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        Method deserializeMethod;
        try {
            deserializeMethod = deserializerInterface.getMethod("deserialize", inputClass);
        } catch (NoSuchMethodException e) {
            throw new MessageNodeVisitorException("Unable to find deserialize method in MessageDeserializer interface!", e);
        }

        deserializeMethodWriter = new MessageDeserializerMethodWriter<>(
            cv.visitMethod(
                ACC_PUBLIC,
                deserializeMethod.getName(),
                Type.getMethodDescriptor(deserializeMethod),
                null,
                null
            ),
            methodReferenceResolver,
            inputClass,
            readerFactoryClass
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
