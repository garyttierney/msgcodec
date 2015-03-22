package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.cgen.ByteBufferMethodReferenceResolver;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.TestMessage;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.PropertyNode;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public class ReleaseGeneratorTest {

    ReleaseGenerator generator = new ReleaseGenerator(new ByteBufferMethodReferenceResolver());

    @Test
    public void testCreateMessageSerializer() throws Exception {
        MessageNode node = new MessageNode();
        node.setIdentifier("org.apollo.extension.releasegen.message.TestMessage");

        PropertyNode propertyNode = new PropertyNode();
        propertyNode.setIdentifier("test");

        IntegerPropertyType propertyType = new IntegerPropertyType();
        propertyType.setBits(32);
        propertyType.setSigned(true);

        propertyNode.setType(propertyType);

        node.addProperty(propertyNode);
        MessageDeserializer deserializer = generator.createMessageDeserializer(node);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(1000);
        buffer.flip();

        TestMessage testMessage = (TestMessage) deserializer.deserialize(buffer);
        Assert.assertEquals(1000, testMessage.getTest());
    }

    @Test
    public void testCreateMessageDeserializer() throws Exception {

    }
}