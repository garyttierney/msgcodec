package org.apollo.extension.releasegen.message.property;

public interface PropertyType {
    Class<?> getType() throws ClassNotFoundException;
}
