package sfix.msgcodec.message.codec;

import sfix.msgcodec.message.node.AttributeType;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.parser.MessageParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for generating parsing a set of message configuration files and creating a {@link MessageCodec} instance.
 */
public class MessageCodecGenerator {

    /**
     * FileVisitor for walking a directory structure and finding message configuration files.
     */
    private static class MessageConfigFileVisitor extends SimpleFileVisitor<Path> {
        public Set<Path> messageConfigPaths = new HashSet<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            messageConfigPaths.add(file);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        public Set<Path> getMessageConfigPaths() {
            return messageConfigPaths;
        }
    }

    /**
     * The message configuration parser instance.
     */
    private final MessageParser messageParser = Parboiled.createParser(MessageParser.class);

    /**
     * The message (de)serializer factory.
     */
    private final MessageSerializerFactory messageSerializerFactory;

    /**
     * A set of files to be parsed by the {@link MessageParser}.
     */
    private final Set<Path> messageConfigPaths = new HashSet<>();

    /**
     * Create a new {@link MessageCodec} generator using <code>messageConfigPath</code> as a search path for message configuration files,
     * and <code>messageSerializerFactory</code> as the factory for creating message (de)serializers.
     *
     * @param messageSerializerFactory The message serializer factory responsible for creating message serializers and deserializers.
     * @param messageConfigPath The path to search for message configuration files.
     *
     * @throws IOException If an error occurred when searching the message configuration path.
     * @see sfix.msgcodec.message.codec.cgen.ASM5MessageSerializerFactory
     * @see sfix.msgcodec.message.codec.reflection.ReflectionMessageSerializerFactory
     */
    public MessageCodecGenerator(MessageSerializerFactory messageSerializerFactory, Path messageConfigPath) throws IOException {
        MessageConfigFileVisitor configFileVisitor = new MessageConfigFileVisitor();
        Files.walkFileTree(messageConfigPath, configFileVisitor);

        this.messageConfigPaths.addAll(configFileVisitor.getMessageConfigPaths());
        this.messageSerializerFactory = messageSerializerFactory;
    }

    public <D> MessageCodec<D> generate(MessageDiscriminatorStrategy<D> discriminatorStrategy) throws MessageCodecGeneratorException {
        ParseRunner<MessageNode> parseRunner = new RecoveringParseRunner<>(messageParser.messageNode());

        final Map<D, MessageDeserializer> deserializerMap = new HashMap<>();
        final Map<Class<?>, MessageSerializer> serializerMap = new HashMap<>();

        for (Path configPath : messageConfigPaths) {
            try {
                ParsingResult<MessageNode> messageParsingResult = parseRunner.run((CharSequence) new String(Files.readAllBytes(configPath)));
                MessageNode node = messageParsingResult.resultValue;
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

            } catch (IOException e) {
                throw new MessageCodecGeneratorException("Unable to read message config file \"" + configPath.toString() + "\"", e);
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
