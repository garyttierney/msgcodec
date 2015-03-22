package org.apollo.extension.releasegen.message;

public interface MessageSerializer<D> {
    D serialize(Object message);
}
