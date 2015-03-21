package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.node.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;


public class MessageDeserializerClassWriter implements MessageNodeVisitor {
    private final ClassVisitor cv;
    private final String className;

    private MessageDeserializerMethodWriter deserializeMethodWriter;

    public MessageDeserializerClassWriter(String className, ClassVisitor classVisitor) {
        this.cv = classVisitor;
        this.className = className;
    }

    @Override
    public void visit(MessageNode messageNode) {
        String className = this.className.replace('.', '/');
        String interfaceClassName = Type.getInternalName(MessageDeserializer.class);

        cv.visit(V1_7,
                ACC_PUBLIC | ACC_SUPER,
                className, null, "java/lang/Object", new String[] { interfaceClassName }
        );

        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

        mv.visitMaxs(1, 1);
        mv.visitVarInsn(ALOAD, 0); // push `this` to the operand stack
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);

        deserializeMethodWriter = new MessageDeserializerMethodWriter(
            cv.visitMethod(
                ACC_PUBLIC,
                "deserialize",
                "(Ljava/nio/ByteBuffer;)Ljava/lang/Object;",
                null,
                null
            )
        );

        deserializeMethodWriter.visit(messageNode);
    }

    @Override
    public void visitCompoundProperty(CompoundPropertyNode node) {
        deserializeMethodWriter.visitCompoundProperty(node);
    }

    @Override
    public void visitPropertyNode(PropertyNode node) {
        deserializeMethodWriter.visitPropertyNode(node);
    }

    @Override
    public void visitEnd(MessageNode node) {
        deserializeMethodWriter.visitEnd(node);
    }

}
