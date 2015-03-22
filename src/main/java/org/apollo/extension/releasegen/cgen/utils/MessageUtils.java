package org.apollo.extension.releasegen.cgen.utils;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MessageUtils {
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
}
