package org.apollo.extension.releasegen.cgen.utils;

import org.objectweb.asm.Opcodes;

import java.beans.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ASMUtils {
    private static final Collection<Class<?>> intTypes = Collections.<Class<?>>unmodifiableCollection(Arrays.asList(int.class, short.class, byte.class, long.class));

    private static String fieldToSetter(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private static String fieldToGetter(Class<?> clazz, String propertyName) {
        if (clazz == boolean.class) {
            return "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        } else {
            return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
    }

    public static  PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {

        PropertyDescriptor resultPropertyDescriptor = null;


        char[] pNameArray = propertyName.toCharArray();
        pNameArray[0] = Character.toLowerCase(pNameArray[0]);
        propertyName = new String(pNameArray);

        try {
            resultPropertyDescriptor =
                new PropertyDescriptor(propertyName, clazz, fieldToGetter(clazz, propertyName), fieldToSetter(propertyName));
        } catch (IntrospectionException e1) {
            // Read-only and write-only properties will throw this
            // exception.  The properties must be looked up using
            // brute force.

            // This will get the list of all properties and iterate
            // through looking for one that matches the property
            // name passed into the method.
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

                PropertyDescriptor[] propertyDescriptors =
                    beanInfo.getPropertyDescriptors();

                for (int i = 0; i < propertyDescriptors.length; i++) {
                    if (propertyDescriptors[i]
                        .getName()
                        .equals(propertyName)) {

                        // If the names match, this this describes the
                        // property being searched for.  Break out of
                        // the iteration.
                        resultPropertyDescriptor = propertyDescriptors[i];
                        break;
                    }
                }
            } catch (IntrospectionException e2) {
                e2.printStackTrace();
            }
        }

        // If no property descriptor was found, then this bean does not
        // have a property matching the name passed in.
        if (resultPropertyDescriptor == null) {
            System.out.println("resultPropertyDescriptor == null");
        }

        return resultPropertyDescriptor;
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

