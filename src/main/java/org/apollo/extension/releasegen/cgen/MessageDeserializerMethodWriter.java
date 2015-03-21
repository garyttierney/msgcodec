package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.node.*;
import org.apollo.extension.releasegen.message.property.ArrayPropertyType;
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
    /**
     * Constant for the buffer parameter slot
     */
    private static final int BUFFER_SLOT = 1;

    /**
     * Constant for the message local var slot
     */
    private static final int MESSAGE_SLOT = 2;

    /**
     * Constant for the variable name of the buffer
     */
    private static final String BUFFER_NAME = "buffer";

    /**
     * Constant for the variable name of the message
     */
    private static final String MESSAGE_NAME = "msg";

    /**
     * Reference to the ASM MethodVisitor which generates the code for the {@link org.apollo.extension.releasegen.message.MessageDeserializer#deserialize} method.
     */
    private final MethodVisitor methodWriter;

    /**
     * Label which marks the start of the methods execution.
     */
    private final Label startLabel = new Label();

    /**
     * Label which marks the end of the methods execution.
     */
    private final Label endLabel = new Label();

    /**
     * Introspection information on the type of Message we're deserializing.
     */
    private BeanInfo messageInfo;

    /**
     * Local variable count, start at 3 to allocate space for constants.
     */
    private final LocalVarManager localVarManager;

    /**
     * The Message class which will be decoded by the generated deserializer.
     */
    private Class<?> messageClass;

    public MessageDeserializerMethodWriter(MethodVisitor writer) {
        this.methodWriter = writer;
        this.localVarManager = new LocalVarManager(writer);
    }

    @Override
    public void visit(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitCode();
        methodWriter.visitLabel(startLabel);

        try {
            messageClass = Class.forName(node.getIdentifier());
            messageInfo = Introspector.getBeanInfo(messageClass);
        } catch (IntrospectionException | ClassNotFoundException e) {
            throw new MessageNodeVisitorException("Failed to get java bean info for message class \"" + node.getIdentifier() + "\"");
        }
    }

    @Override
    public void visitCompoundProperty(CompoundPropertyNode node) {
    }

    @Override
    public void visitPropertyNode(PropertyNode node) throws MessageNodeVisitorException{
        PropertyType propertyType = node.getType();
        try {
            if (propertyType instanceof IntegerPropertyType) {
                visitIntPropertyNode(node, (IntegerPropertyType) propertyType);
            } else if (propertyType instanceof ArrayPropertyType) {
                visitArrayPropertyNode(node, (ArrayPropertyType) propertyType);
            } else {
                visitObjectPropertyNode(node, propertyType);
            }
        } catch (Exception ex) {
            throw new MessageNodeVisitorException("Unable to create bytecode to deserialize property \"" + node.getIdentifier() + "\"", ex);
        }
    }

    private void visitObjectPropertyNode(PropertyNode node, PropertyType propertyType) {

    }

    public void readAndStoreIntPropertyNode(PropertyNode node, IntegerPropertyType type) {
        Class<?> intType = type.getType();

        int slot = localVarManager.allocate(node.getIdentifier(), intType);

        methodWriter.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(ByteBuffer.class),
                MessageUtils.getByteBufferReadMethod(type),
                Type.getMethodDescriptor(Type.getType(type.getType())),
                false
        );

        localVarManager.store(slot); // store method result to local var
     }

    public void visitIntPropertyNode(PropertyNode node, IntegerPropertyType type) {
        readAndStoreIntPropertyNode(node, type); // read and store value
        localVarManager.push(node); // push back to stack

        PropertyDescriptor descriptor = getPropertyDescriptor(node.getIdentifier());
        Method writeMethod = descriptor.getWriteMethod();

        // call setter with local var
        methodWriter.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(messageClass), writeMethod.getName(), Type.getMethodDescriptor(writeMethod), false);
    }

    public void visitCompoundArrayPropertyNode(CompoundPropertyNode node, ArrayPropertyType type) {

    }

    public void visitArrayPropertyNode(PropertyNode node, ArrayPropertyType type) throws ClassNotFoundException {
        Class<?> valueType = type.getType();
        PropertyType elementType = type.getElementType();

        pushArrayLength(type.getLengthSpecifier());

        if (elementType instanceof IntegerPropertyType) {
            methodWriter.visitIntInsn(NEWARRAY, T_INT);
        } else {
            methodWriter.visitTypeInsn(ANEWARRAY, Type.getInternalName(valueType));
        }

        int slot = localVarManager.allocate(node.getIdentifier(), valueType);
        methodWriter.visitVarInsn(ASTORE, slot);


        Label loopEndLabel = new Label();
        Label loopStartLabel = new Label();
        methodWriter.visitLabel(loopStartLabel);

        int counterSlot = localVarManager.allocate("counter_" + node.getIdentifier(), int.class);

        { // counter = 0
            methodWriter.visitInsn(ICONST_0);
            localVarManager.store(counterSlot);
        }

        Label loopLabel = new Label();

        { // counter < length
            methodWriter.visitLabel(loopLabel);
            localVarManager.push(counterSlot);
            pushArrayLength(type.getLengthSpecifier());
            methodWriter.visitJumpInsn(IF_ICMPGE, loopEndLabel);
        }

        methodWriter.visitLabel(loopEndLabel);

    }

    @Override
    public void visitEnd(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitVarInsn(ALOAD, MESSAGE_SLOT);
        methodWriter.visitInsn(ARETURN);
        methodWriter.visitLabel(endLabel);

        localVarManager.visitLocalVariables();

        methodWriter.visitLocalVariable(BUFFER_NAME, Type.getDescriptor(ByteBuffer.class), null, startLabel, endLabel, BUFFER_SLOT);
        methodWriter.visitLocalVariable(MESSAGE_NAME, Type.getDescriptor(Object.class), null, startLabel, endLabel, MESSAGE_SLOT);
        methodWriter.visitMaxs(1, 1); //@automatically resolved by options set in class writer
        methodWriter.visitEnd();

    }

    public int getLocalVarSlot(String propertyName) {
        for(Map.Entry<Integer, String> node : localVarMap.entrySet()) {
            if (node.getValue().equals(propertyName)) {
                return node.getKey();
            }
        }

        return -1;
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        for (PropertyDescriptor descriptor : messageInfo.getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName)) {
                return descriptor;
            }
        }

        return null;
    }

    public void pushArrayLength(String lengthSpecifier) {
        boolean lengthSpecifierIsConst = lengthSpecifier.matches("[0-9]+");

        // push size of array to the stack
        if (lengthSpecifierIsConst) {
            methodWriter.visitIntInsn(SIPUSH, Integer.valueOf(lengthSpecifier));
        } else { // array size is identifier
            methodWriter.visitVarInsn(ALOAD, getLocalVarSlot(lengthSpecifier));
        }
    }
}
