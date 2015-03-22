package org.apollo.extension.releasegen.cgen;

import java.lang.reflect.Method;

public class MethodReference {
    private final Class<?> owner;

    public Class<?> getOwner() {
        return owner;
    }

    public Method getMethod() {
        return method;
    }

    private final Method method;

    public MethodReference(Class<?> owner, Method method) {
        this.owner = owner;
        this.method = method;
    }
}
