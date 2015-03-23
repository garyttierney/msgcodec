package sfix.msgcodec.message.codec.cgen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sfix.msgcodec.io.DataType;
import sfix.msgcodec.message.codec.MessageSerializerFactory;
import sfix.msgcodec.message.codec.MessageDeserializer;
import sfix.msgcodec.message.TestMessage;
import sfix.msgcodec.message.node.CompoundPropertyNode;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.node.PropertyNode;
import sfix.msgcodec.message.property.ArrayPropertyType;
import sfix.msgcodec.message.property.IntegerPropertyType;
import sfix.msgcodec.message.property.PropertyType;
import sfix.msgcodec.message.property.SimplePropertyType;
import org.junit.Assert;
import org.junit.Test;

public class ASM5MessageSerializerFactoryTest {
    @Test
    public void testCreateMessageDeserializer() throws Exception {
        MessageNode node = new MessageNode();
        node.setIdentifier("sfix.msgcodec.message.TestMessage");

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("test");

            IntegerPropertyType propertyType = new IntegerPropertyType();
            propertyType.setDataType(DataType.INT);
            propertyType.setSigned(true);

            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("testB");

            IntegerPropertyType propertyType = new IntegerPropertyType();
            propertyType.setDataType(DataType.SHORT);
            propertyType.setSigned(true);

            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("testC");

            IntegerPropertyType elementType = new IntegerPropertyType();
            elementType.setDataType(DataType.SHORT);
            elementType.setSigned(true);

            ArrayPropertyType propertyType = new ArrayPropertyType(elementType, "5");
            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            CompoundPropertyNode propertyNode = new CompoundPropertyNode();
            propertyNode.setIdentifier("testD");

            PropertyType elementType = new SimplePropertyType("sfix.msgcodec.message.TestCompoundMessage");
            ArrayPropertyType propertyType = new ArrayPropertyType(elementType, "2");

            propertyNode.setType(propertyType);

            {
                PropertyNode childPropertyNode = new PropertyNode();
                childPropertyNode.setIdentifier("propertyA");

                IntegerPropertyType childPropertyType = new IntegerPropertyType();
                childPropertyType.setDataType(DataType.SHORT);
                childPropertyType.setSigned(true);

                childPropertyNode.setType(childPropertyType);
                propertyNode.addChild(childPropertyNode);
            }

            {
                PropertyNode childPropertyNode = new PropertyNode();
                childPropertyNode.setIdentifier("propertyB");

                IntegerPropertyType childPropertyType = new IntegerPropertyType();
                childPropertyType.setDataType(DataType.SHORT);
                childPropertyType.setSigned(true);

                childPropertyNode.setType(childPropertyType);
                propertyNode.addChild(childPropertyNode);
            }


            node.addProperty(propertyNode);

        }

        MessageSerializerFactory codecFactory = new ASM5MessageSerializerFactory();
        MessageDeserializer deserializer = codecFactory.createDeserializer(node);

        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(1000);
        buffer.writeShort((short) 6);
        for(int i = 0; i < 5; i++) {
            buffer.writeShort((short) 100);
        }
        for(int i = 0; i < 2; i++) {
            buffer.writeShort((short) 1);
            buffer.writeShort(3);
        }
        buffer.resetReaderIndex();

        TestMessage testMessage = (TestMessage) deserializer.deserialize(buffer);
        Assert.assertEquals(1000, testMessage.getTest());
        Assert.assertEquals(6, testMessage.getTestB());

        short[] testC = testMessage.getTestC();

        Assert.assertEquals(5, testC.length);
        Assert.assertEquals(2, testMessage.getTestD().length);
    }

    @Test
    public void testCreateSerializer() throws Exception {

    }
}