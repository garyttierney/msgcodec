package sfix.msgcodec.message;

import sfix.msgcodec.message.node.MessageNode;

import java.util.Collection;

public interface MessageLoader {
    Collection<MessageNode> load() throws MessageLoaderException;
}
