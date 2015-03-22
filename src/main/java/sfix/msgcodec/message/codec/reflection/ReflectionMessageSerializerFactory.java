package sfix.msgcodec.message.codec.reflection;

import sfix.msgcodec.message.codec.MessageDeserializer;
import sfix.msgcodec.message.codec.MessageSerializer;
import sfix.msgcodec.message.codec.MessageSerializerFactory;
import sfix.msgcodec.message.codec.MessageSerializerFactoryException;
import sfix.msgcodec.message.node.MessageNode;

public class ReflectionMessageSerializerFactory implements MessageSerializerFactory{
    @Override
    public MessageDeserializer createDeserializer(MessageNode messageNode) throws MessageSerializerFactoryException {
        return null;
    }

    @Override
    public MessageSerializer createSerializer(MessageNode messageNode) throws MessageSerializerFactoryException {
        return null;
    }
}
