package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.node.*;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.apollo.extension.releasegen.message.property.PropertyType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class MessageDeserializerMethodWriter implements MessageNodeVisitor {
    private final MethodVisitor methodWriter;

    private final Label startLabel = new Label();
    private final Label endLabel = new Label();

    private static final int BUFFER_SLOT = 1;
    private static final int MESSAGE_SLOT = 2;
    private static final String BUFFER_NAME = "buffer";
    private static final String MESSAGE_NAME = "msg";

    private final Map<Integer, PropertyNode> localVarMap = new HashMap<>();
    private BeanInfo messageInfo;

    private int localVarCount = 3;
    private Class<?> messageClass;

    public int allocateLocalVar(PropertyNode propertyNode) {
        localVarMap.put(localVarCount, propertyNode);

        return localVarCount++;
    }

    public MessageDeserializerMethodWriter(MethodVisitor writer) {
        this.methodWriter = writer;
    }

    @Override
    public void visit(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitCode();
        methodWriter.visitLabel(startLabel);

        try {
            messageClass = Class.forName(node.getIdentifier());
            messageInfo = Introspector.getBeanInfo(messageClass);

        } catch (IntrospectionException | ClassNotFoundException e ) {
            throw new MessageNodeVisitorException("Failed to get java bean info for message class \"" + node.getIdentifier() + "\"");
        }
    }

    @Override
    public void visitCompoundProperty(CompoundPropertyNode node) {

    }

    @Override
    public void visitPropertyNode(PropertyNode node) {
        PropertyType propertyType = node.getType();
        if (propertyType instanceof IntegerPropertyType) {
            visitIntPropertyNode(node, (IntegerPropertyType) propertyType);
        }
    }

    public void visitIntPropertyNode(PropertyNode node, IntegerPropertyType type) {
        int localVarSlot = allocateLocalVar(node);

        methodWriter.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(ByteBuffer.class),
                MessageUtils.getByteBufferReadMethod(type),
                Type.getMethodDescriptor(Type.getType(type.getType())),
                false
        );

        methodWriter.visitVarInsn(ISTORE, localVarSlot);
        methodWriter.visitVarInsn(ILOAD, localVarSlot);

        PropertyDescriptor descriptor = getPropertyDescriptor(node.getIdentifier());
        Method writeMethod = descriptor.getWriteMethod();

        methodWriter.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(messageClass), writeMethod.getName(), Type.getMethodDescriptor(writeMethod), false);
    }

    @Override
    public void visitEnd(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitVarInsn(ALOAD, MESSAGE_SLOT);
        methodWriter.visitInsn(ARETURN);
        methodWriter.visitLabel(endLabel);

        for(Map.Entry<Integer, PropertyNode> localVar : localVarMap.entrySet()) {
            int slot = localVar.getKey();

            PropertyNode propertyNode = localVar.getValue();
            PropertyType propertyType = propertyNode.getType();
            Class<?> valueType;
            try {
                valueType = propertyType.getType();
            } catch (ClassNotFoundException e) {
                throw new MessageNodeVisitorException("Couldn't instantiate class for property \"" + propertyNode.getIdentifier() + "\" " +
                        "in \"" + messageClass.getCanonicalName() + "\"", e);
            }

            methodWriter.visitLocalVariable(
                localVar.getValue().getIdentifier(),
                Type.getDescriptor(valueType),
                null,
                startLabel,
                endLabel,
                slot
            );
        }

        methodWriter.visitLocalVariable(BUFFER_NAME, Type.getDescriptor(ByteBuffer.class), null, startLabel, endLabel, BUFFER_SLOT);
        methodWriter.visitLocalVariable(MESSAGE_NAME, Type.getDescriptor(Object.class), null, startLabel, endLabel, MESSAGE_SLOT);
        methodWriter.visitMaxs(1, 1); //@automatically resolved by options set in class writer
        methodWriter.visitEnd();

    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        for(PropertyDescriptor descriptor : messageInfo.getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName)) {
                return descriptor;
            }
        }

        return null;
    }

}
