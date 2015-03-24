package sfix.msgcodec.message.parser;

import sfix.msgcodec.io.DataOrder;
import sfix.msgcodec.io.DataType;
import sfix.msgcodec.message.node.*;
import sfix.msgcodec.message.property.ArrayPropertyType;
import sfix.msgcodec.message.property.IntegerPropertyType;
import sfix.msgcodec.message.property.PropertyType;
import sfix.msgcodec.message.property.SimplePropertyType;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.support.Var;

@BuildParseTree
public class MessageParser extends BaseParser<Object> {
    public Rule SEMICOLON = ch(';').skipNode();
    public Rule EQ = ch('=').skipNode();
    public Rule LBRACE = ch('{').skipNode();
    public Rule RBRACE = ch('}').skipNode();
    public Rule LBRACKET = ch('[').suppressNode();
    public Rule RBRACKET = ch(']').suppressNode();
    public Rule UNDERSCORE = ch('_').suppressNode();


    /**
     * Match a full message node description and push the parsed result to the stack. Example of a full message description:
     * <pre>
     * org.apollo.game.message.SomeMessage :opcode => 50, :type => "server" {
     *     uint16 propertyA;
     *
     *     org.apollo.game.message.SomeEmbeddedMessage propertyB[propertyA] {
     *         uint16 subPropertyA;
     *         uint32 subPropertyB;
     *     }
     *
     *     bool propertyC;
     * }
     * </pre>
     *
     * @return A rule which matches a full {@link sfix.msgcodec.message.node.MessageNode}.
     */
    @Label("message")
    public Rule messageNode() {
        final Var<MessageNode> nodeVar = new Var<>(new MessageNode());
        
        return sequence(
            qualifiedIdentifier(),
            nodeVar.getNonnull().setIdentifier((String) pop()),
            optional(
                sequence(
                    attribute(), nodeVar.getNonnull().addAttribute((AttributeNode) pop()),
                    zeroOrMore(
                        sequence(ch(','), attribute(), nodeVar.getNonnull().addAttribute((AttributeNode) pop()))
                    )
                )
            ),
            zeroOrMore(
                sequence(attribute(), nodeVar.getNonnull().addAttribute((AttributeNode) pop()))
            ),
            oneOrMore(
                sequence(spacing(), firstOf(compoundPropertyDefinition(), propertyDefinition()), nodeVar.getNonnull().addProperty((PropertyNode) pop()))
            ),
            push(nodeVar.getAndSet(new MessageNode()))
        );
    }

    /**
     * Match a fully qualified java class name identifier, example:
     * <pre>
     *     org.apollo.game.message.SomeMessage
     * </pre>
     *
     * @return A rule matching a fully qualified class name.
     */
    @Label("qualified_identifier")
    public Rule qualifiedIdentifier() {
        return sequence(sequence(identifier(), zeroOrMore(ch('.'), identifier())), push(match()));
    }

    /**
     * Match an identifier, or variable name. Only letters or digits.
     *
     * @return A rule matching an identifier.
     */
    @Label("identifier")
    public Rule identifier() {
        return sequence(letter(), zeroOrMore(firstOf(letter(), digit())));
    }

    /**
     * Match a {@link sfix.msgcodec.message.node.CompoundPropertyNode} with the following structure:
     * <pre>
     * # (optional array syntax, identifier[size] where size = a constant or previously declared var
     * org.apollo.fully.qualified.Name identifier {
     *     uint32 propertyA;
     *     uint16 propertyB;
     * }
     * </pre>
     *
     * @return A rule that matches and constructs a CompoundPropertyNode.
     */
    @Label("compound_property")
    public Rule compoundPropertyDefinition() {
        final Var<CompoundPropertyNode> propertyVar = new Var<>(new CompoundPropertyNode());

        return firstOf(
            // match array
            sequence(
                qualifiedIdentifier(), spacing(), identifier(), propertyVar.getNonnull().setIdentifier(match()),
                propertyArrayInitializer(),
                propertyVar.getNonnull().setType(
                    new ArrayPropertyType(new SimplePropertyType((String) pop(1)), (String) pop())
                ),
                spacing(),
                LBRACE,
                oneOrMore(
                    sequence(spacing(), propertyDefinition(), propertyVar.getNonnull().addChild((PropertyNode) pop()), spacing()).label("property_decl")
                ).label("child_properties"),
                RBRACE, spacing(),
                push(propertyVar.getNonnull())
            ).label("array_definition"),

            // match simple object
            sequence(
                qualifiedIdentifier().label("type"), spacing(), identifier(), propertyVar.getNonnull().setIdentifier(match()),
                propertyVar.getNonnull().setType(
                    new SimplePropertyType((String) pop())
                ),
                spacing(),
                LBRACE, spacing(),
                oneOrMore(
                    sequence(spacing(), propertyDefinition(), propertyVar.getNonnull().addChild((PropertyNode) pop()), spacing()).label("property_decl")
                ).label("child_properties"),
                RBRACE, spacing(),
                push(propertyVar.getAndSet(new CompoundPropertyNode()))
            ).label("definition")
        );
    }

    /**
     * Matches a property definition, with the type and identifier and pushes the created node to the stack. Examples are:
     * <pre>
     *      int32 propertyA;
     *      bool propertyB;
     *      string propertyC;
     *      string propertyD[propertyA];
     * </pre>
     *
     * @return A rule which matches one of the above property definitions.
     */
    @Label("property_definition")
    public Rule propertyDefinition() {
        Var<PropertyNode> propertyNodeVar = new Var<>(new PropertyNode());


        return sequence(
            spacing(), propertyType(), spacing(), propertyNodeVar.set(new PropertyNode()), propertyNodeVar.getNonnull().setType((PropertyType) pop()),

            firstOf(
                sequence(
                    identifier(),
                    propertyNodeVar.getNonnull().setIdentifier(match()),
                    propertyArrayInitializer(),
                    propertyNodeVar.getNonnull().setType(new ArrayPropertyType(propertyNodeVar.getNonnull().getType(), (String) pop()))
                ),
                sequence(identifier(), propertyNodeVar.getNonnull().setIdentifier(match()))
            ),

            push(propertyNodeVar.getAndSet(new PropertyNode())),
            SEMICOLON
        );
    }

    /**
     * Matches a property array initializer, similar to java syntax. Matches the following:
     * <pre>
     *     [property_identifier] // reference to prev declared var
     *     [500] // constant number
     * </pre>
     *
     * @return A rule which matches an array initializer.
     */
    @Label("property_array_initializer")
    public Rule propertyArrayInitializer() {
        return sequence(
            LBRACKET,
            sequence(
                firstOf(
                    identifier(), number()
                ),
                push(match())
            ),
            RBRACKET
        ).suppressNode();
    }

    /**
     * Matches an attribute declaration, similar to ruby hash synax. Matches the following:
     * <pre>
     *     :identifier => 200
     *     :identifier => "literal"
     *     :identifier => constant
     * </pre>
     *
     * @return A rule which matches an attribute.
     */
    @Label("attribute")
    public Rule attribute() {
        Var<AttributeNode> attributeNodeVar = new Var<>(new AttributeNode());

        return sequence(
            ch(':').suppressNode(), identifier(), attributeNodeVar.getNonnull().setIdentifier(match()),
            spacing(), string("=>"), spacing(),
            attributeValue(), attributeNodeVar.getNonnull().setValue(match()), attributeNodeVar.getNonnull().setType((AttributeType) pop()), spacing(),
            push(attributeNodeVar.getAndSet(new AttributeNode()))
        );
    }

    /**
     * Match a value in an attribute declaration. It can be either a number, a string literal, or a reference to a previously declared identifier.
     * Possible values are:
     * <pre>
     *     "string_literal"
     *     0xcafebabe - no longer supported, for now
     *     12345
     *     previouslyDeclaredVariable
     * </pre>
     *
     * @return A rule matching an attribute value.
     */
    @Label("attribute_value")
    public Rule attributeValue() {
        return firstOf(
            sequence(number(), push(AttributeType.NUMBER_LITERAL)),
            sequence(stringLiteral(), push(AttributeType.STRING_LITERAL)),
            sequence(identifier(), push(AttributeType.REFERENCE))
        );
    }

    /**
     * Match a property type declaration and push a {@link PropertyType} to the stack.
     *
     * @return A rule which matches any property type.
     * @see #stringType
     * @see #booleanType
     * @see #intType
     */
    @Label("property_type")
    public Rule propertyType() {
        return firstOf(stringType(), booleanType(), intType());
    }

    /**
     * A rule matching "string", upon being matched it will push a String property type to the stack.
     *
     * @return A rule matching "string".
     */
    public Rule stringType() {
        return sequence(string("string"), push(new SimplePropertyType("java.lang.String")));
    }

    /**
     * A rule which matches a C style integer type declaration, it supports both signed and unsigned types and allows you to specify any number of bits. Examples:
     * <pre>
     *     uint32
     *     int8
     *     int32
     *     uint16
     * </pre>
     * <p/>
     * <p/>
     * Upon being matched a {@link IntegerPropertyType} will be pushed to the stack.
     *
     * @return A rule matching an integer type.
     */
    public Rule intType() {
        Var<IntegerPropertyType> intTypeVar = new Var<>(new IntegerPropertyType());
        
        return sequence(
            optional(sequence(ch('u'), intTypeVar.getNonnull().setSigned(false))).suppressSubnodes(),
            string("int").suppressNode(),
            sequence(
                oneOrMore(digit()),
                intTypeVar.getNonnull().setDataType(
                    DataType.fromBits(Integer.valueOf(match()))
                ),
                optional(sequence(intTypeDataOrder(), intTypeVar.getNonnull().setDataOrder((DataOrder) pop())))
            ),
            push(intTypeVar.getAndSet(new IntegerPropertyType()))
        );
    }

    public Rule intTypeDataOrder() {
        String[] orderIdentifiers = DataOrder.identifiers();
        Rule[] orderRules = new Rule[orderIdentifiers.length];

        for (int i = 0; i < orderRules.length; i++) {
            orderRules[i] = string(orderIdentifiers[i]);
        }

        return sequence(UNDERSCORE, firstOf(orderRules), push(DataOrder.from(match())));
    }

    /**
     * A rule matching either "Boolean", or "bool" which pushes a boolean property type to the stack.
     *
     * @return A rule matching either "Boolean", or "bool"
     */
    public Rule booleanType() {
        return sequence(firstOf(ignoreCase("boolean"), ignoreCase("bool")), push(new SimplePropertyType("boolean")));
    }

    /**
     * A rule matching a number literal.
     *
     * @return A rule matching a number literal.
     */
    public Rule number() {
        return oneOrMore(digit());
    }

    /**
     * Matches a letter independent of it being uppercase or lowercase.
     *
     * @return A rule matching an uppercase or lowercase letter.
     */
    public Rule letter() {
        return firstOf(charRange('a', 'z'), charRange('A', 'Z'));
    }

    /**
     * Matches a string literal, excluding new lines.
     *
     * @return A rule which matches a string literal.
     * @todo - fix this so it discards quotes
     */
    public Rule stringLiteral() {
        return sequence(
            ch('"').skipNode(),
            zeroOrMore(
                sequence(testNot(anyOf("\r\n\"\\")), ANY)
            ).suppressSubnodes(),
            ch('"').skipNode()
        );
    }

    @SuppressNode
    public Rule spacing() { //from the parboiled java parser, supports whitespace, docblocks and end of line comments
        return zeroOrMore(firstOf(

            // whitespace
            oneOrMore(anyOf(" \t\r\n\f").label("Whitespace")),

            // traditional comment
            sequence("/*", zeroOrMore(testNot("*/"), ANY), "*/"),

            // end of line comment
            sequence(
                "//",
                zeroOrMore(testNot(anyOf("\r\n")), ANY),
                firstOf("\r\n", '\r', '\n', EOI)
            )
        ));
    }

}
