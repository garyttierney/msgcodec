package sfix.msgcodec.message.codec;

import org.parboiled.Parboiled;
import sfix.msgcodec.message.DefaultMessageLoader;
import sfix.msgcodec.message.MessageLoader;
import sfix.msgcodec.message.MessageLoaderException;
import sfix.msgcodec.message.node.AttributeType;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.parser.MessageParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for generating parsing a set of message configuration files and creating a {@link MessageCodec} instance.
 */
public class MessageCodecGenerator {


    /**
     * The message configuration parser instance.
     */
    private final MessageParser messageParser = Parboiled.createParser(MessageParser.class);

    /**
     * The message (de)serializer factory.
     */
    private final MessageSerializerFactory messageSerializerFactory;

    /**
     * The MessageLoader implementation responsible for loading a collection of {@link MessageNode}s.
     */
    private final MessageLoader messageLoader;

    /**
     * Create a new {@link MessageCodec} generator using the {@link DefaultMessageLoader} implementation and <code>messageConfigPath</code> as a search path for message configuration files,
     * with <code>messageSerializerFactory</code> as the factory for creating message (de)serializers.
     *
     * @param messageSerializerFactory The message serializer factory responsible for creating message serializers and deserializers.
     * @param messageConfigPath The path to search for message configuration files.
     *
     * @throws IOException If an error occurred when searching the message configuration path.
     */
    public MessageCodecGenerator(MessageSerializerFactory messageSerializerFactory, Path messageConfigPath) throws IOException {
        this(messageSerializerFactory, new DefaultMessageLoader(messageConfigPath));
    }

    /**
     * Create a new {@link MessageCodec} generator using the specified MessageLoader and MessageSerializerFactory.
     *
     * @param messageSerializerFactory The {@link MessageSerializerFactory} to use for generating {@link MessageSerializer} and {@link MessageDeserializer}
     *                                 implentations.
     * @param messageLoader The MessageLoader to use for loading {@link MessageNode}s
     *
     * @see sfix.msgcodec.message.codec.cgen.ASM5MessageSerializerFactory
     * @see sfix.msgcodec.message.codec.reflection.ReflectionMessageSerializerFactory
     */
    public MessageCodecGenerator(MessageSerializerFactory messageSerializerFactory, MessageLoader messageLoader) {
        this.messageSerializerFactory = messageSerializerFactory;
        this.messageLoader = messageLoader;
    }

    public <D> MessageCodec<D> generate(MessageDiscriminatorStrategy<D> discriminatorStrategy) throws MessageCodecGeneratorException {
        final Map<D, MessageDeserializer> deserializerMap = new HashMap<>();
        final Map<Class<?>, MessageSerializer> serializerMap = new HashMap<>();

        final Set<MessageNode> messageNodes = new HashSet<>();
        try {
            messageNodes.addAll(messageLoader.load());
        } catch (MessageLoaderException ex) {
            throw new MessageCodecGeneratorException("Unable to load a collection of MessageNodes to generate serilization classes for", ex);
        }

        for (MessageNode node : messageNodes) {
            if (!node.hasAttribute("type")) {
                throw new MessageCodecGeneratorException("Message configuration file for \"" + node.getIdentifier() + "\" does not have a \"type\" attribute");
            }

            if (!node.hasAttribute("opcode") || node.getAttribute("opcode").getType() != AttributeType.NUMBER_LITERAL) {
                throw new MessageCodecGeneratorException("Message configuration file for \"" + node.getIdentifier() + "\" does not have a valid \"opcode\" attribute");
            }

            D discriminator = discriminatorStrategy.getDiscriminator(node);

            try {
                serializerMap.put(Class.forName(node.getIdentifier()), messageSerializerFactory.createSerializer(node));
                deserializerMap.put(discriminator, messageSerializerFactory.createDeserializer(node));
            } catch (Exception ex) {
                throw new MessageCodecGeneratorException("Error occurred when creating serializer or deserializer for \"" + node.getIdentifier() + "\"", ex);
            }
        }

        return new MessageCodec<>(deserializerMap, serializerMap);

    }

    /**
     * Generate MessageCodec using an integer attribute named "opcode" as the message discriminator.
     *
     * @return An instance of MessageCodec with an Integer as the discriminator.
     * @throws MessageCodecGeneratorException If an error occurred when generating the MessageCodec.
     */
    public MessageCodec<Class<?>> generate() throws MessageCodecGeneratorException {
        return generate(new MessageClassDiscriminatorStrategy());
    }
}
