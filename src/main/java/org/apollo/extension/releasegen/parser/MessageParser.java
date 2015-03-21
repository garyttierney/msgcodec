package org.apollo.extension.releasegen.parser;

import org.apollo.extension.releasegen.parser.message.node.AttributeNode;
import org.apollo.extension.releasegen.parser.message.node.AttributeType;
import org.apollo.extension.releasegen.parser.message.node.MessageNode;
import org.apollo.extension.releasegen.parser.message.node.PropertyNode;
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
    private final Rule SEMICOLON = ch(';').skipNode();

    Rule MessageNode() {
        MessageNode node = new MessageNode();

        return sequence(
                messageIdentifier(),
                node.setIdentifier((String) pop()),
                zeroOrMore(
                        sequence(attribute(), node.addAttribute((AttributeNode) pop()))
                ),
                oneOrMore(
                        CompoundPropertyDefinition(null),
                        PropertyDefinition()
                ),
                push(node)
        );
    }

    @Label("qualified_message_identifier")
    Rule messageIdentifier() {
        return sequence(sequence(identifier(), zeroOrMore(ch('.'), identifier())), push(match()));
    }

    @Label("identifier")
    Rule identifier() {
        return sequence(Letter(), zeroOrMore(firstOf(Letter(), Digit())));
    }

    @Label("compound_property")
    Rule CompoundPropertyDefinition(MessageNode messageNode) {
        return null;
    }

    @Label("property")
    Rule PropertyDefinition() {
        PropertyNode messagePropertyNode = new PropertyNode();

        return sequence(
                PropertyType(), messagePropertyNode.setType((PropertyType) pop()),
                sequence(identifier(), messagePropertyNode.setIdentifier(match())),
                SEMICOLON
        );
    }

    @Label("attribute")
    Rule attribute() {
        AttributeNode attributeNode = new AttributeNode();

        return sequence(
                identifier(), attributeNode.setIdentifier(match()),
                Spacing(), ch('=').suppressNode(), Spacing(),
                AttributeValue(), attributeNode.setValue(match()), attributeNode.setType((AttributeType) pop()),
                push(attributeNode)
        );
    }


    @Label("attribute_value")
    Rule AttributeValue() {
        return firstOf(
                sequence(Number(), push(AttributeType.NUMBER_LITERAL)),
                sequence(StringLiteral(), push(AttributeType.STRING_LITERAL)),
                sequence(identifier(), push(AttributeType.REFERENCE))
        );
    }

    Rule PropertyType() {
        return firstOf(StringType(), BooleanType(), IntType());
    }

    Rule StringType() {
        return sequence(string("string"), push(new SimplePropertyType(String.class)));
    }

    Rule IntType() {
        IntegerPropertyType intType = new IntegerPropertyType();

        return sequence(
                optional(sequence(ch('u'), intType.setSigned(false))).suppressSubnodes(),
                string("int").suppressNode(),
                Number(), intType.setBits(Integer.valueOf(match())),
                push(intType)
        );
    }

    Rule BooleanType() {
        return sequence(firstOf(String("Boolean"), String("bool")), push(new SimplePropertyType(boolean.class)));
    }

    Rule Number() {
        return OneOrMore(Digit());
    }

    Rule LetterOrDigitOrUnderscore() {
        return firstOf(Letter(), Digit());
    }

    Rule Letter() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule StringLiteral() {
        return sequence(
                '"',
                ZeroOrMore(
                        firstOf(
                                Escape(),
                                sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
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
                sequence(CharRange('0', '3'), CharRange('0', '7'), CharRange('0', '7')),
                sequence(CharRange('0', '7'), CharRange('0', '7')),
                CharRange('0', '7')
        );
    }

    Rule UnicodeEscape() {
        return sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    Rule HexDigit() {
        return firstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
    }

    @SuppressNode
    Rule Spacing() { //from the parboiled java parser, supports whitespace, docblocks and end of line comments
        return ZeroOrMore(firstOf(

                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

                // traditional comment
                sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),

                // end of line comment
                sequence(
                        "//",
                        ZeroOrMore(TestNot(AnyOf("\r\n")), ANY),
                        firstOf("\r\n", '\r', '\n', EOI)
                )
        ));
    }
}
