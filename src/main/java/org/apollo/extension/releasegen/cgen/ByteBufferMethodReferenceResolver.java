package org.apollo.extension.releasegen.cgen;

import org.apollo.extension.releasegen.message.property.PropertyType;

public class ByteBufferMethodReferenceResolver implements MethodReferenceResolver {
    @Override
    public MethodReference getReadMethod(PropertyType type) {
        return null;
    }

    @Override
    public MethodReference getWriteMethod(PropertyType type) {
        return null;
    }
}
