package org.apollo.extension.releasegen.cgen.utils;

import org.objectweb.asm.Opcodes;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ASMUtils {
    private static final Collection<Class<?>> intTypes = Collections.<Class<?>>unmodifiableCollection(Arrays.asList(int.class, short.class, byte.class, long.class));

    public static PropertyDescriptor getPropertyDescriptor(BeanInfo info, String propertyName) {
        for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName)) {
                return descriptor;
            }
        }

        return null;
    }

    public static boolean isIntegerType(Class<?> type) {
        return intTypes.contains(type);
    }

    public static int getIntegerArrayStoreInsn(Class<?> type) {
        if (type == int.class) {
            return Opcodes.IASTORE;
        } else if (type == short.class) {
            return Opcodes.SASTORE;
        } else if (type == byte.class) {
            return Opcodes.BASTORE;
        } else if (type == long.class) {
            return Opcodes.LASTORE;
        }

        return -1;
    }

    public static int getIntegerArrayLoadInsn(Class<?> type) {
        if (type == int.class) {
            return Opcodes.IALOAD;
        } else if (type == short.class) {
            return Opcodes.SALOAD;
        } else if (type == byte.class) {
            return Opcodes.BALOAD;
        } else if (type == long.class) {
            return Opcodes.LALOAD;
        }

        return -1;
    }

    public static int getIntegerArrayType(Class<?> type) {
        if (type == int.class) {
            return Opcodes.T_INT;
        } else if (type == short.class) {
            return Opcodes.T_SHORT;
        } else if (type == byte.class) {
            return Opcodes.T_BYTE;
        } else if (type == long.class) {
            return Opcodes.T_LONG;
        }

        return -1;
    }
}

