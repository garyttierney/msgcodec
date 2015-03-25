package sfix.msgcodec.message.codec.cgen.utils;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.Set;

/**
 * A class responsible for managing the LocalVariables and parameters of a method.
 */
public class LocalVarManager {
    private final MethodVisitor methodVisitor;
    private final Label methodStartLabel;
    private final Label methodEndLabel;
    private Set<LocalVarEntry> localVarEntries = new HashSet<>();

    private int localVarCount = 4;

    /**
     * Create a new LocalVariableManager for the specified methodVisitor within the specified scope.
     *
     * @todo - add support for localVarCount or refactor other code to use this
     * @param methodVisitor The method visitor to use when creating new var instructions.
     * @param methodStartLabel The start label of the method.
     * @param methodEndLabel The end label of the method.
     */
    public LocalVarManager(MethodVisitor methodVisitor, Label methodStartLabel, Label methodEndLabel) {
        this.methodVisitor = methodVisitor;
        this.methodStartLabel = methodStartLabel;
        this.methodEndLabel = methodEndLabel;
    }

    /**
     * Lookup a local variable slot by name and if it doesn't exist then create it.
     *
     * @return The slot of the newly allocated or found local variable.
     * @see #allocate(String, Class, Label, Label)
     */
    public int getOrAllocate(String name, Class<?> type) {
        LocalVarEntry entry = findByName(name);
        if (entry != null) {
            return entry.slot;
        }

        return allocate(name, type);
    }

    /**
     * Allocates a local variable on the stack with the default scope.
     *
     * @see #allocate(String, Class, Label, Label)
     */
    public int allocate(String name, Class<?> type) {
        return allocate(name, type, null, null);
    }

    /**
     * Allocate a new local variable on the stack.
     *
     * @param name The name of the new local variable.
     * @param type The type represented by this local variable.
     * @param startLabel The beginning of this local variables scope. If null, it defaults to the start of the method.
     * @param endLabel The end of this local variables scope. If null, it defaults to the end of the method.
     *
     * @return The slot of the allocated local variable.
     */
    public int allocate(String name, Class<?> type, Label startLabel, Label endLabel) {
        LocalVarEntry entry = new LocalVarEntry();
        entry.slot = localVarCount++;
        entry.type = type;
        entry.name = name;
        entry.startLabel = startLabel;
        entry.endLabel = endLabel;

        localVarEntries.add(entry);

        return entry.slot;
    }

    /**
     * Pop a value from the stack and lookup a local variable slot to store it in.
     *
     * @param slot The slot of the local variable.
     * @see #store(LocalVarEntry)
     */
    public void store(int slot) {
        LocalVarEntry entry = findBySlot(slot);
        store(entry);
    }

    /**
     * Pop a value from stack and lookup a local variable name to store it in.
     *
     * @param name The name of the local variable.
     * @see #store(LocalVarEntry)
     */
    public void store(String name) {
        LocalVarEntry entry = findByName(name);
        store(entry);
    }

    /**
     * Pop a value from the stack and store it in the local variable specified by <code>entry</code>.
     *
     * @param entry The entry to store the value in.
     */
    public void store(LocalVarEntry entry) {
        methodVisitor.visitVarInsn(Type.getType(entry.type).getOpcode(Opcodes.ISTORE), entry.slot);
    }

    /**
     * Load a value from a local variable specified by <code>entry</code> and push it to the stack.
     *
     * @param entry The entry to push to the stack.
     */
    public void push(LocalVarEntry entry) {
        methodVisitor.visitVarInsn(Type.getType(entry.type).getOpcode(Opcodes.ILOAD), entry.slot);
    }

    /**
     * Lookup a local variable by slot and push it to the stack.
     *
     * @param slot The local variable slot.
     * @see #push(LocalVarEntry)
     */
    public void push(int slot) {
        LocalVarEntry entry = findBySlot(slot);
        push(entry);
    }

    /**
     * Lookup a local variable by name and push it to the stack
     *
     * @param name The local variable name.
     * @see #push(LocalVarEntry)
     */
    public void push(String name) {
        LocalVarEntry entry = findByName(name);
        push(entry);
    }

    public int getSlot(String name) {
        return findByName(name).slot;
    }

    public final LocalVarEntry findBySlot(int slot) {
        for (LocalVarEntry entry : localVarEntries) {
            if (entry.slot == slot) {
                return entry;
            }
        }

        return null;
    }

    public final LocalVarEntry findByName(String name) {
        for (LocalVarEntry entry : localVarEntries) {
            if (entry.name.equals(name)) {
                return entry;
            }
        }

        return null;
    }

    public void visitLocalVariables() {
        for (LocalVarEntry e : localVarEntries) {
            methodVisitor.visitLocalVariable(
                e.name,
                Type.getDescriptor(e.type),
                null,
                e.startLabel == null ? this.methodStartLabel : e.startLabel,
                e.endLabel == null ? this.methodEndLabel : e.endLabel,
                e.slot
            );
        }
    }

    static class LocalVarEntry {
        protected String name;
        protected Class<?> type;
        protected int slot;
        protected Label startLabel;
        protected Label endLabel;
    }

}
