package org.apollo.extension.releasegen;

public class ReleaseGeneratorClassLoader extends ClassLoader {
    public ReleaseGeneratorClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClassProxy(String name, byte[] b, int off, int len) {
        return defineClass(name, b, off, len);
    }
}
