package org.apollo.extension.releasegen;

public class MessageCodecGeneratorException extends Exception {
    public MessageCodecGeneratorException(String message, Exception prev) {
        super(message, prev);
    }

    public MessageCodecGeneratorException(String message) {
        super(message);
    }
}
