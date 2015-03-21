package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.PropertyNode;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.junit.Test;

public class ReleaseGeneratorTest {

    ReleaseGenerator generator = new ReleaseGenerator();

    @Test
    public void testCreateMessageSerializer() throws Exception {
        MessageNode node = new MessageNode();
        PropertyNode propertyNode = new PropertyNode();
        propertyNode.setIdentifier("org.apollo.extension.releasegen.message.TestMessage");

        IntegerPropertyType propertyType = new IntegerPropertyType();
        propertyType.setBits(32);
        propertyType.setSigned(true);

        propertyNode.setType(propertyType);

        MessageDeserializer deserializer = generator.createMessageDeserializer(node);

    }

    @Test
    public void testCreateMessageDeserializer() throws Exception {

    }
}