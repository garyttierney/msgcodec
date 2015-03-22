package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.cgen.ByteBufferMethodReferenceResolver;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.TestMessage;
import org.apollo.extension.releasegen.message.node.CompoundPropertyNode;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.PropertyNode;
import org.apollo.extension.releasegen.message.property.ArrayPropertyType;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.apollo.extension.releasegen.message.property.PropertyType;
import org.apollo.extension.releasegen.message.property.SimplePropertyType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ReleaseGeneratorTest {

    ReleaseGenerator generator = new ReleaseGenerator(new ByteBufferMethodReferenceResolver());

    @Test
    public void testCreateMessageSerializer() throws Exception {
        MessageNode node = new MessageNode();
        node.setIdentifier("org.apollo.extension.releasegen.message.TestMessage");

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("test");

            IntegerPropertyType propertyType = new IntegerPropertyType();
            propertyType.setBits(32);
            propertyType.setSigned(true);

            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("testB");

            IntegerPropertyType propertyType = new IntegerPropertyType();
            propertyType.setBits(16);
            propertyType.setSigned(true);

            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.setIdentifier("testC");

            IntegerPropertyType elementType = new IntegerPropertyType();
            elementType.setBits(16);
            elementType.setSigned(true);

            ArrayPropertyType propertyType = new ArrayPropertyType(elementType, "5");
            propertyNode.setType(propertyType);

            node.addProperty(propertyNode);
        }

        {
            CompoundPropertyNode propertyNode = new CompoundPropertyNode();
            propertyNode.setIdentifier("testD");

            PropertyType elementType = new SimplePropertyType("org.apollo.extension.releasegen.message.TestCompoundMessage");
            ArrayPropertyType propertyType = new ArrayPropertyType(elementType, "2");

            propertyNode.setType(propertyType);

            {
                PropertyNode childPropertyNode = new PropertyNode();
                childPropertyNode.setIdentifier("propertyA");

                IntegerPropertyType childPropertyType = new IntegerPropertyType();
                childPropertyType.setBits(16);
                childPropertyType.setSigned(true);

                childPropertyNode.setType(childPropertyType);
                propertyNode.addChild(childPropertyNode);
            }

            {
                PropertyNode childPropertyNode = new PropertyNode();
                childPropertyNode.setIdentifier("propertyB");

                IntegerPropertyType childPropertyType = new IntegerPropertyType();
                childPropertyType.setBits(32);
                childPropertyType.setSigned(true);

                childPropertyNode.setType(childPropertyType);
                propertyNode.addChild(childPropertyNode);
            }


            node.addProperty(propertyNode);

        }

        MessageDeserializer deserializer = generator.createMessageDeserializer(node);

        ByteBuffer buffer = ByteBuffer.allocate(6 + (5 * 2) + 12);
        buffer.putInt(1000);
        buffer.putShort((short) 6);
        for(int i = 0; i < 5; i++) {
            buffer.putShort((short) 100);
        }
        for(int i = 0; i < 2; i++) {
            buffer.putShort((short) 1);
            buffer.putInt(3);
        }
        buffer.flip();

        TestMessage testMessage = (TestMessage) deserializer.deserialize(buffer);
        Assert.assertEquals(1000, testMessage.getTest());
        Assert.assertEquals(6, testMessage.getTestB());

        short[] testC = testMessage.getTestC();

        Assert.assertEquals(5, testC.length);
        Assert.assertEquals(2, testMessage.getTestD().length);
    }

    @Test
    public void testCreateMessageDeserializer() throws Exception {

    }

}