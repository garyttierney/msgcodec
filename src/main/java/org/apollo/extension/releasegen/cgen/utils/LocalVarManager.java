package org.apollo.extension.releasegen.cgen.utils;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.Set;

public class LocalVarManager {
    private final MethodVisitor methodVisitor;
    private final Label methodStartLabel;
    private final Label methodEndLabel;
    private Set<LocalVarEntry> localVarEntries = new HashSet<>();

    private int localVarCount = 3;

    public LocalVarManager(MethodVisitor methodVisitor, Label methodStartLabel, Label methodEndLabel) {
        this.methodVisitor = methodVisitor;
        this.methodStartLabel = methodStartLabel;
        this.methodEndLabel = methodEndLabel;
    }

    public int getOrAllocate(String name, Class<?> type) {
        LocalVarEntry entry = findByName(name);
        if (entry != null) {
            return entry.slot;
        }

        return allocate(name, type);
    }

    public int allocate(String name, Class<?> type) {
        return allocate(name, type, null, null);
    }

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

    public void store(int slot) {
        LocalVarEntry entry = findBySlot(slot);
        store(entry);
    }

    public void store(String name) {
        LocalVarEntry entry = findByName(name);
        store(entry);
    }

    public void store(LocalVarEntry entry) {
        if (ASMUtils.isIntegerType(entry.type)) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, entry.slot);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, entry.slot);
        }
     }

    public void push(LocalVarEntry entry) {
        if (ASMUtils.isIntegerType(entry.type)) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, entry.slot);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, entry.slot);
        }
    }

    public void push(int slot) {
        LocalVarEntry entry = findBySlot(slot);
        push(entry);
    }

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
