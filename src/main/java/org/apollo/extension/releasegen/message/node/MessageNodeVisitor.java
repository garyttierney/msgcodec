package org.apollo.extension.releasegen.message.node;

public interface MessageNodeVisitor {
    void visit(MessageNode node) throws MessageNodeVisitorException;

    void visitCompoundProperty(CompoundPropertyNode node) throws MessageNodeVisitorException;

    void visitPropertyNode(PropertyNode node) throws MessageNodeVisitorException;

    void visitEnd(MessageNode node) throws MessageNodeVisitorException;
}
