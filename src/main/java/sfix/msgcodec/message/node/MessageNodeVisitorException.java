package sfix.msgcodec.message.node;

public class MessageNodeVisitorException extends Exception {
    public MessageNodeVisitorException(String message) {
        super(message);
    }

    public MessageNodeVisitorException(String message, Exception prev) {
        super(message, prev);
    }
}
