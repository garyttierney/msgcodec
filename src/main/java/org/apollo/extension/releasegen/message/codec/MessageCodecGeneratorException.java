package org.apollo.extension.releasegen.message.codec;

public class MessageCodecGeneratorException extends Exception {
    public MessageCodecGeneratorException(String message, Exception prev) {
        super(message, prev);
    }

    public MessageCodecGeneratorException(String message) {
        super(message);
    }
}
