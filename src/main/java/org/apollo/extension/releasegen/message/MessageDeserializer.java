package org.apollo.extension.releasegen.message;

public interface MessageDeserializer<T> {
    Object deserialize(T input);
}
