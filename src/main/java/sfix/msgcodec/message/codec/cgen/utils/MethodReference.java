package sfix.msgcodec.message.codec.cgen.utils;

import java.lang.reflect.Method;

/**
 * Represents a reference to a Method which reads or writes a property.
 */
public class MethodReference {
    private final Class<?> owner;
    private final Method method;

    public MethodReference(Class<?> owner, Method method) {
        this.owner = owner;
        this.method = method;
    }

    /**
     * The owner of the Method.
     *
     * @return The class which owns the method.
     */
    public Class<?> getOwner() {
        return owner;
    }

    /**
     * The method which reads or writes the property.
     *
     * @return Reference to the method which reads or writes the property.
     */
    public Method getMethod() {
        return method;
    }
}
