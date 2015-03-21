package org.apollo.extension.releasegen.parser;

import org.apollo.extension.releasegen.parser.message.node.AttributeNode;
import org.apollo.extension.releasegen.parser.message.node.AttributeType;
import org.apollo.extension.releasegen.parser.message.node.MessageNode;
import org.apollo.extension.releasegen.parser.message.node.PropertyNode;
import org.apollo.extension.releasegen.parser.message.property.ArrayPropertyType;
import org.apollo.extension.releasegen.parser.message.property.IntegerPropertyType;
import org.apollo.extension.releasegen.parser.message.property.PropertyType;
import org.apollo.extension.releasegen.parser.message.property.SimplePropertyType;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SuppressNode;

@BuildParseTree
public class MessageParser extends BaseParser<Object> {
    Rule SEMICOLON = ch(';').skipNode();
    Rule EQ = ch('=').skipNode();
    Rule LBRACE = ch('{').skipNode();
    Rule RBRACE = ch('}').skipNode();
    Rule LBRACKET = ch('[').skipNode();
    Rule RBRACKET = ch(']').skipNode();


    @Label("message")
    Rule messageNode() {
        MessageNode node = new MessageNode();

        return sequence(
                qualifiedIdentifier(),
                node.setIdentifier((String) pop()),
                zeroOrMore(
                        sequence(attribute(), node.addAttribute((AttributeNode) pop()))
                ),
                oneOrMore(
                        compoundPropertyDefinition(),
                        propertyDefinition()
                ),
                push(node)
        );
    }

    @Label("qualified_identifier")
    Rule qualifiedIdentifier() {
        return sequence(sequence(identifier(), zeroOrMore(ch('.'), identifier())), push(match()));
    }

    @Label("identifier")
    Rule identifier() {
        return sequence(letter(), zeroOrMore(firstOf(letter(), digit())));
    }

    @Label("compound_property")
    Rule compoundPropertyDefinition() {
        return sequence(
                qualifiedIdentifier(), letter()
        );
    }

    @Label("property")
    Rule propertyDefinition() {
        PropertyNode messagePropertyNode = new PropertyNode();

        return sequence(
                propertyType(), messagePropertyNode.setType((PropertyType) pop()),

                firstOf(
                        sequence(
                                identifier(),
                                messagePropertyNode.setIdentifier(match()),
                                propertyArrayInitializer(),
                                messagePropertyNode.setType(new ArrayPropertyType(messagePropertyNode.getType(), (String) pop()))
                        ),
                        sequence(identifier(), messagePropertyNode.setIdentifier(match()))
                ),

                push(messagePropertyNode),
                SEMICOLON
        );
    }

    @Label("property_array_initializer")
    Rule propertyArrayInitializer() {
        return sequence(
                LBRACKET,
                sequence(
                        firstOf(
                                identifier(), number()
                        ),
                        push(match())
                ),
                RBRACKET
        );
    }

    @Label("attribute")
    Rule attribute() {
        AttributeNode attributeNode = new AttributeNode();

        return sequence(
                identifier(), attributeNode.setIdentifier(match()),
                Spacing(), EQ, Spacing(),
                attributeValue(), attributeNode.setValue(match()), attributeNode.setType((AttributeType) pop()),
                push(attributeNode)
        );
    }


    @Label("attribute_value")
    Rule attributeValue() {
        return firstOf(
                sequence(number(), push(AttributeType.NUMBER_LITERAL)),
                sequence(stringLiteral(), push(AttributeType.STRING_LITERAL)),
                sequence(identifier(), push(AttributeType.REFERENCE))
        );
    }

    @Label("property_type")
    Rule propertyType() {
        return firstOf(stringType(), booleanType(), intType());
    }

    Rule stringType() {
        return sequence(string("string"), push(new SimplePropertyType(String.class)));
    }

    Rule intType() {
        IntegerPropertyType intType = new IntegerPropertyType();

        return sequence(
                optional(sequence(ch('u'), intType.setSigned(false))).suppressSubnodes(),
                string("int").suppressNode(),
                number(), intType.setBits(Integer.valueOf(match())),
                push(intType)
        );
    }

    Rule booleanType() {
        return sequence(firstOf(String("Boolean"), String("bool")), push(new SimplePropertyType(boolean.class)));
    }

    Rule number() {
        return oneOrMore(digit());
    }

    Rule letter() {
        return firstOf(charRange('a', 'z'), charRange('A', 'Z'));
    }

    Rule stringLiteral() {
        return sequence(
                '"',
                zeroOrMore(
                        firstOf(
                                Escape(),
                                sequence(testNot(anyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                '"'
        );
    }

    Rule Escape() {
        return sequence('\\', firstOf(AnyOf("btnfr\"\'\\"), OctalEscape(), UnicodeEscape()));
    }

    Rule OctalEscape() {
        return firstOf(
                sequence(charRange('0', '3'), charRange('0', '7'), charRange('0', '7')),
                sequence(charRange('0', '7'), charRange('0', '7')),
                charRange('0', '7')
        );
    }

    Rule UnicodeEscape() {
        return sequence(oneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    Rule HexDigit() {
        return firstOf(charRange('a', 'f'), charRange('A', 'F'), charRange('0', '9'));
    }

    @SuppressNode
    Rule Spacing() { //from the parboiled java parser, supports whitespace, docblocks and end of line comments
        return zeroOrMore(firstOf(

                // whitespace
                oneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

                // traditional comment
                sequence("/*", zeroOrMore(TestNot("*/"), ANY), "*/"),

                // end of line comment
                sequence(
                        "//",
                        zeroOrMore(TestNot(AnyOf("\r\n")), ANY),
                        firstOf("\r\n", '\r', '\n', EOI)
                )
        ));
    }

}
