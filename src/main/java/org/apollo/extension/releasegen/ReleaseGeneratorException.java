package org.apollo.extension.releasegen;

public class ReleaseGeneratorException extends Exception {
    public ReleaseGeneratorException(String message, Exception prev) {
        super(message, prev);
    }

    public ReleaseGeneratorException(String message) {
        super(message);
    }
}
