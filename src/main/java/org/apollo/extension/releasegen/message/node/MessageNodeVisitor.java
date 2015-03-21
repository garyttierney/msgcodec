package org.apollo.extension.releasegen.message.node;

public interface MessageNodeVisitor {
    void visit(MessageNode node);

    void visitCompoundProperty(CompoundPropertyNode node);

    void visitPropertyNode(PropertyNode node);

    void visitEnd(MessageNode node);
}
