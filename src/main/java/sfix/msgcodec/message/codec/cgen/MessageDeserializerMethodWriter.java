package sfix.msgcodec.message.codec.cgen;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import sfix.msgcodec.io.DataOrder;
import sfix.msgcodec.io.DataTransformation;
import sfix.msgcodec.io.DataType;
import sfix.msgcodec.message.codec.cgen.utils.ASMUtils;
import sfix.msgcodec.message.codec.cgen.utils.LocalVarManager;
import sfix.msgcodec.message.node.*;
import sfix.msgcodec.message.property.ArrayPropertyType;
import sfix.msgcodec.message.property.IntegerPropertyType;
import sfix.msgcodec.message.property.PropertyType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

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
     * Reference to the ASM MethodVisitor which generates the code for the {@link sfix.msgcodec.message.codec.MessageDeserializer#deserialize} method.
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
     * Local variable manager for dealing with tracking slots of local vars.
     */
    private final LocalVarManager localVarManager;

    /**
     * Introspection information on the type of Message we're de-serializing.
     */
    private BeanInfo messageInfo;

    /**
     * The Message class which will be decoded by the generated deserializer.
     */
    private Class<?> messageClass;

    public MessageDeserializerMethodWriter(MethodVisitor writer) {
        this.methodWriter = writer;
        this.localVarManager = new LocalVarManager(writer, startLabel, endLabel);
    }

    public static String getChildPropertyName(PropertyNode parent, PropertyNode child) {
        return parent.getIdentifier() + "$" + child.getIdentifier();
    }

    @Override
    public void visit(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitCode();
        methodWriter.visitLabel(startLabel);

        try {
            messageClass = Class.forName(node.getIdentifier());
            messageInfo = Introspector.getBeanInfo(messageClass);

            methodWriter.visitTypeInsn(NEW, Type.getInternalName(messageClass));
            methodWriter.visitInsn(DUP);
            methodWriter.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(messageClass),
                "<init>",
                Type.getConstructorDescriptor(messageClass.getConstructor()),
                false
            );
            methodWriter.visitVarInsn(ASTORE, MESSAGE_SLOT);
        } catch (IntrospectionException | ClassNotFoundException e) {
            throw new MessageNodeVisitorException("Failed to get java bean info for message class \"" + node.getIdentifier() + "\"", e);
        } catch (NoSuchMethodException e) {
            throw new MessageNodeVisitorException("Couldn't find no-args constructor for message class \"" + messageClass.getCanonicalName() + "\"", e);
        }
    }

    @Override
    public void visitPropertyNode(PropertyNode node) throws MessageNodeVisitorException {
        PropertyType propertyType = node.getType();

        try {
            if (propertyType instanceof ArrayPropertyType) {
                visitArrayPropertyNode(node, (ArrayPropertyType) propertyType);
            } else {
                int slot = node instanceof CompoundPropertyNode ?
                    readAndStoreCompoundVar((CompoundPropertyNode) node, node.getType()) :
                    readAndStoreVar(node);

                methodWriter.visitVarInsn(ALOAD, MESSAGE_SLOT);
                localVarManager.push(slot); // push back to stack

                PropertyDescriptor descriptor = ASMUtils.getPropertyDescriptor(messageInfo, node.getIdentifier());
                Method writeMethod = descriptor.getWriteMethod();

                // call setter with local var
                methodWriter.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(messageClass), writeMethod.getName(), Type.getMethodDescriptor(writeMethod), false);
            }
        } catch (Exception ex) {
            throw new MessageNodeVisitorException("Unable to create bytecode to deserialize property \"" + node.getIdentifier() + "\"", ex);
        }
    }

    public int readAndStoreVar(PropertyNode node) throws ClassNotFoundException, NoSuchMethodException {
        int slot = localVarManager.allocate(node.getIdentifier(), node.getType().getType());

        readAndStoreVar(slot, node.getType());
        return slot;
    }

    public void readAndStoreVar(int slot, PropertyType type) throws NoSuchMethodException, ClassNotFoundException {
        MethodReference ref = PacketMethodReferenceResolver.getReadMethod(type);
        Method method = ref.getMethod();

        methodWriter.visitVarInsn(ALOAD, BUFFER_SLOT);
        if (type instanceof IntegerPropertyType) {
            IntegerPropertyType intType = (IntegerPropertyType) type;
            methodWriter.visitFieldInsn(PUTSTATIC, Type.getInternalName(DataType.class), intType.getDataType().name(), Type.getDescriptor(DataType.class));
            methodWriter.visitFieldInsn(PUTSTATIC, Type.getInternalName(DataOrder.class), intType.getDataOrder().name(), Type.getDescriptor(DataOrder.class));
            methodWriter.visitFieldInsn(
                PUTSTATIC,
                Type.getInternalName(DataTransformation.class),
                intType.getDataTransformation().name(),
                Type.getDescriptor(DataTransformation.class)
            );
        }

        methodWriter.visitMethodInsn(
            INVOKEVIRTUAL,
            Type.getInternalName(ref.getOwner()),
            method.getName(),
            Type.getMethodDescriptor(method),
            false
        );

        localVarManager.store(slot);

    }

    public void visitArrayPropertyNode(PropertyNode node, ArrayPropertyType type) throws ClassNotFoundException, IntrospectionException, NoSuchMethodException {
        Class<?> valueType = type.getType();
        PropertyType elementType = type.getElementType();

        pushArrayLength(type.getLengthSpecifier());

        if (elementType instanceof IntegerPropertyType) {
            methodWriter.visitIntInsn(NEWARRAY, ASMUtils.getIntegerArrayType(elementType.getType()));
        } else {
            methodWriter.visitTypeInsn(ANEWARRAY, Type.getInternalName(elementType.getType()));
        }

        int slot = localVarManager.allocate(node.getIdentifier(), valueType);
        localVarManager.store(slot);

        Label loopStartLabel = new Label();
        Label loopEndLabel = new Label();
        Label loopConditionLabel = new Label();

        methodWriter.visitLabel(loopStartLabel);

        int counterSlot = localVarManager.allocate("counter_" + node.getIdentifier(), int.class);

        { // counter = 0
            methodWriter.visitInsn(ICONST_0);
            localVarManager.store(counterSlot);

            methodWriter.visitJumpInsn(GOTO, loopConditionLabel);
        }

        Label loopLabel = new Label();

        { // identifier[counter++] = buffer.getValue()
            methodWriter.visitLabel(loopLabel);

            int elementSlot = localVarManager.allocate(node.getIdentifier() + "_el", elementType.getType(), loopLabel, loopEndLabel);
            if (node instanceof CompoundPropertyNode) {
                readAndStoreCompoundVar(elementSlot, (CompoundPropertyNode) node, elementType);
            } else {
                readAndStoreVar(elementSlot, elementType);
            }

            localVarManager.push(slot);
            localVarManager.push(counterSlot);
            localVarManager.push(elementSlot);

            if (elementType instanceof IntegerPropertyType) {
                methodWriter.visitInsn(ASMUtils.getIntegerArrayStoreInsn(elementType.getType()));
            } else {
                methodWriter.visitInsn(AASTORE);
            }

            methodWriter.visitIincInsn(counterSlot, 1);

        }

        { // counter < length
            methodWriter.visitLabel(loopConditionLabel);

            localVarManager.push(counterSlot);
            pushArrayLength(type.getLengthSpecifier());

            methodWriter.visitJumpInsn(IF_ICMPLT, loopLabel);
        }

        methodWriter.visitLabel(loopEndLabel);
        methodWriter.visitVarInsn(ALOAD, MESSAGE_SLOT);
        localVarManager.push(slot); // push back to stack

        PropertyDescriptor descriptor = ASMUtils.getPropertyDescriptor(messageInfo, node.getIdentifier());
        Method writeMethod = descriptor.getWriteMethod();

        // call setter with local var
        methodWriter.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(messageClass), writeMethod.getName(), Type.getMethodDescriptor(writeMethod), false);
    }

    public int readAndStoreCompoundVar(CompoundPropertyNode node, PropertyType compoundPropertyType) throws ClassNotFoundException, IntrospectionException, NoSuchMethodException {
        int slot = localVarManager.allocate(node.getIdentifier(), node.getType().getType());

        readAndStoreCompoundVar(slot, node, compoundPropertyType);
        return slot;
    }

    public void readAndStoreCompoundVar(int slot, CompoundPropertyNode node, PropertyType compoundPropertyType) throws ClassNotFoundException,
        IntrospectionException, NoSuchMethodException {

        Class<?> compoundObjectClass = compoundPropertyType.getType();

        methodWriter.visitTypeInsn(NEW, Type.getInternalName(compoundObjectClass));
        methodWriter.visitInsn(DUP);
        methodWriter.visitMethodInsn(
            INVOKESPECIAL,
            Type.getInternalName(compoundObjectClass),
            "<init>",
            Type.getConstructorDescriptor(compoundObjectClass.getConstructor()),
            false
        );

        localVarManager.store(slot);

        for (PropertyNode child : node.getChildren()) {
            PropertyDescriptor propertyDescriptor = ASMUtils.getPropertyDescriptor(Introspector.getBeanInfo(compoundObjectClass), child.getIdentifier());
            Method writeMethod = propertyDescriptor.getWriteMethod();

            int childSlot = localVarManager.getOrAllocate(getChildPropertyName(node, child), child.getType().getType());
            readAndStoreVar(childSlot, child.getType());

            localVarManager.push(slot);
            localVarManager.push(childSlot);
            methodWriter.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(compoundObjectClass), writeMethod.getName(), Type.getMethodDescriptor(writeMethod), false);
        }
    }

    @Override
    public void visitEnd(MessageNode node) throws MessageNodeVisitorException {
        methodWriter.visitVarInsn(ALOAD, MESSAGE_SLOT);
        methodWriter.visitInsn(ARETURN);
        methodWriter.visitLabel(endLabel);

        methodWriter.visitLocalVariable("this", Type.getDescriptor(messageClass), null, startLabel, endLabel, 0);
        methodWriter.visitLocalVariable(BUFFER_NAME, Type.getDescriptor(ByteBuffer.class), null, startLabel, endLabel, BUFFER_SLOT);
        methodWriter.visitLocalVariable(MESSAGE_NAME, Type.getDescriptor(Object.class), null, startLabel, endLabel, MESSAGE_SLOT);

        localVarManager.visitLocalVariables();

        methodWriter.visitMaxs(1, 1);
        methodWriter.visitEnd();
    }

    public void pushArrayLength(String lengthSpecifier) {
        boolean lengthSpecifierIsConst = lengthSpecifier.matches("[0-9]+");

        // push size of array to the stack
        if (lengthSpecifierIsConst) {
            methodWriter.visitIntInsn(SIPUSH, Integer.valueOf(lengthSpecifier));
        } else { // array size is identifier
            localVarManager.push(lengthSpecifier);
        }
    }
}
