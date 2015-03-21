package org.apollo.extension.releasegen.parser;

import org.apollo.extension.releasegen.parser.message.node.AttributeType;
import org.apollo.extension.releasegen.parser.message.node.AttributeNode;
import org.apollo.extension.releasegen.parser.message.node.MessageNode;
import org.apollo.extension.releasegen.parser.message.property.IntegerPropertyType;
import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

public class MessageParserTest {
    MessageParser parser = Parboiled.createParser(MessageParser.class);

    @Test
    public void testMessageIdentifier() throws Exception {
        String input = "org.apollo.message.Whatever";
        ParsingResult<?> result = new RecoveringParseRunner<>(parser.messageIdentifier()).run(input);
        String identifier = (String) result.resultValue;

        Assert.assertEquals(input, identifier);
    }

    @Test
    public void testCompoundPropertyDefinition() throws Exception {

    }

    @Test
    public void testPropertyDefinition() throws Exception {

    }

    @Test
    public void testAttribute_Reference() throws Exception {
        String input = "test = whateverValue";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.attribute()).run(input);
        AttributeNode attribute = (AttributeNode) result.resultValue;

        Assert.assertEquals(AttributeType.REFERENCE, attribute.getType());
        Assert.assertEquals("whateverValue", attribute.getValue());
        Assert.assertEquals("test", attribute.getIdentifier());
    }

    @Test
    public void testAttribute_NumberLiteral() throws Exception {
        String input = "test = 1001";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.attribute()).run(input);
        AttributeNode attribute = (AttributeNode) result.resultValue;

        Assert.assertEquals(AttributeType.NUMBER_LITERAL, attribute.getType());
        Assert.assertEquals("1001", attribute.getValue());
        Assert.assertEquals("test", attribute.getIdentifier());
    }

    @Test
    public void testAttribute_StringLiteral() throws Exception {
        String input = "test = \"1001\"";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.attribute()).run(input);
        AttributeNode attribute = (AttributeNode) result.resultValue;

        Assert.assertEquals(AttributeType.STRING_LITERAL, attribute.getType());
        Assert.assertEquals("\"1001\"", attribute.getValue());
        Assert.assertEquals("test", attribute.getIdentifier());
    }


    @Test
    public void testAttributeValue() throws Exception {

    }

    @Test
    public void testPropertyType_Uint32() throws Exception {
        String input = "uint32";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.PropertyType()).run(input);
        IntegerPropertyType attribute = (IntegerPropertyType) result.resultValue;

        Assert.assertEquals(32, attribute.getBits());
        Assert.assertFalse(attribute.isSigned());
    }
}