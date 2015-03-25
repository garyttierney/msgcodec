package sfix.msgcodec.message.utils;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

public class MessageUtils {

    public static PropertyDescriptor getPropertyDescriptor(BeanInfo info, String propertyName) {
        for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName)) {
                return descriptor;
            }
        }

        return null;
    }

}

