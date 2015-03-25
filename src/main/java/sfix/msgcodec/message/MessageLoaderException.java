package sfix.msgcodec.message;

public class MessageLoaderException extends Exception {
    public MessageLoaderException(String message, Throwable prev) {
        super(message, prev);
    }

    /**
     * Constructs a new throwable with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     * <p/>
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public MessageLoaderException(String message) {
        super(message);
    }
}
