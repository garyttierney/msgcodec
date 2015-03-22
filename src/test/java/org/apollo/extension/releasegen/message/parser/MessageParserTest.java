package org.apollo.extension.releasegen.message.parser;

import org.apollo.extension.releasegen.message.node.*;
import org.apollo.extension.releasegen.message.property.ArrayPropertyType;
import org.apollo.extension.releasegen.message.property.IntegerPropertyType;
import org.junit.Assert;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

public class MessageParserTest {
    MessageParser parser = Parboiled.createParser(MessageParser.class);

    @Test
    public void testMessageNode() throws Exception {
        String input =
                "org.apollo.message.TestMessage :opcode => 50 { \n" +
                "    uint32 testPropertyA; \n" +
                "    org.apollo.message.TestEmbeddedMessage testPropertyB { \n" +
                "        uint16 testPropertyC; \n" +
                "    }\n" +
                "}";

        ParsingResult<MessageNode> result = new RecoveringParseRunner<MessageNode>(parser.messageNode()).run(input);
        MessageNode message = result.resultValue;
        System.err.println(ParseTreeUtils.printNodeTree(result));
        Assert.assertEquals("org.apollo.message.TestMessage", message.getIdentifier());
        Assert.assertEquals(2, message.getPropertyList().size());

    }

    @Test
    public void testMessageIdentifier() throws Exception {
        String input = "org.apollo.message.Whatever";
        ParsingResult<?> result = new RecoveringParseRunner<>(parser.qualifiedIdentifier()).run(input);
        String identifier = (String) result.resultValue;

        Assert.assertEquals(input, identifier);
    }

    @Test
    public void testCompoundPropertyDefinition() throws Exception {
        String input =
                "java.lang.String test { \n" +
                "   uint32 testPropertyA; \n" +
                "   uint16 testPropertyB; \n" +
                "}";

        ParsingResult<CompoundPropertyNode> result = new RecoveringParseRunner<CompoundPropertyNode>(parser.compoundPropertyDefinition()).run(input);
        CompoundPropertyNode attribute = result.resultValue;

        Assert.assertEquals(String.class, attribute.getType().getType());

        Assert.assertTrue(attribute.hasChild("testPropertyA"));
        Assert.assertTrue(attribute.hasChild("testPropertyB"));
    }

    @Test
    public void testCompoundPropertyDefinition_Array() throws Exception {
        String input =
                "java.lang.String test[200] { \n" +
                "   uint32 testPropertyA; \n" +
                "   uint16 testPropertyB; \n" +
                "}";

        ParsingResult<CompoundPropertyNode> result = new RecoveringParseRunner<CompoundPropertyNode>(parser.compoundPropertyDefinition()).run(input);
        CompoundPropertyNode attribute = result.resultValue;

        System.err.println(ParseTreeUtils.printNodeTree(result));
        Assert.assertEquals(String[].class, attribute.getType().getType());

        Assert.assertTrue(attribute.hasChild("testPropertyA"));
        Assert.assertTrue(attribute.hasChild("testPropertyB"));
    }

    @Test
    public void testPropertyDefinition() throws Exception {
        String input = "uint32 test;";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.propertyDefinition()).run(input);
        PropertyNode attribute = (PropertyNode) result.resultValue;

        Assert.assertTrue(attribute.getType() instanceof IntegerPropertyType);

    }

    @Test
    public void testPropertyDefinition_Array() throws Exception {
        String input = "int32 test[10];";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.propertyDefinition()).run(input);
        PropertyNode attribute = (PropertyNode) result.resultValue;

        Assert.assertTrue(attribute.getType() instanceof ArrayPropertyType);
        Assert.assertEquals(attribute.getIdentifier(), "test");

        ArrayPropertyType arrayType = (ArrayPropertyType) attribute.getType();
        Assert.assertEquals("10", arrayType.getLengthSpecifier());
        Assert.assertEquals(int[].class, arrayType.getType());
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
    public void testPropertyType_Uint32() throws Exception {
        String input = "uint32";
        ParsingResult<?> result = new RecoveringParseRunner<MessageNode>(parser.propertyType()).run(input);
        IntegerPropertyType attribute = (IntegerPropertyType) result.resultValue;

        Assert.assertEquals(32, attribute.getBits());
        Assert.assertFalse(attribute.isSigned());
    }
}