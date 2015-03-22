package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.cgen.ByteBufferMethodReferenceResolver;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.TestMessage;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.PropertyNode;
import org.apollo.extension.releasegen.message.property.ArrayPropertyType;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
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

        MessageDeserializer deserializer = generator.createMessageDeserializer(node);

        ByteBuffer buffer = ByteBuffer.allocate(6 + (5 * 2));
        buffer.putInt(1000);
        buffer.putShort((short) 6);
        for(int i = 0; i < 5; i++) {
            buffer.putShort((short) 100);
        }
        buffer.flip();

        TestMessage testMessage = (TestMessage) deserializer.deserialize(buffer);
        Assert.assertEquals(1000, testMessage.getTest());
        Assert.assertEquals(6, testMessage.getTestB());

        short[] testC = testMessage.getTestC();

        Assert.assertEquals(5, testC.length);

        System.err.println(Arrays.toString(testC));
    }

    @Test
    public void testCreateMessageDeserializer() throws Exception {

    }

    public static class TestMessageDeserializer {

    }
}