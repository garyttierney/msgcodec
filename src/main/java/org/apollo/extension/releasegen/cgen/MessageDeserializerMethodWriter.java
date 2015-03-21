package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.node.*;
import org.objectweb.asm.MethodVisitor;

public class MessageDeserializerMethodWriter implements MessageNodeVisitor {
    private final MethodVisitor methodWriter;

    public MessageDeserializerMethodWriter(MethodVisitor writer) {
        this.methodWriter = writer;
    }

    @Override
    public void visit(MessageNode node) {
        methodWriter.visitCode();
    }

    @Override
    public void visitCompoundProperty(CompoundPropertyNode node) {

    }

    @Override
    public void visitPropertyNode(PropertyNode node) {

    }

    @Override
    public void visitEnd(MessageNode node) {

    }
}
