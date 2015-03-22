package org.apollo.extension.releasegen.message.cgen;

import org.apollo.extension.releasegen.message.property.PropertyType;

/**
 * Interface definition for looking up the method needed to read or write a {@link PropertyType}.
 */
public interface MethodReferenceResolver {
    public MethodReference getReadMethod(PropertyType type) throws NoSuchMethodException, ClassNotFoundException;

    public MethodReference getWriteMethod(PropertyType type);
}
