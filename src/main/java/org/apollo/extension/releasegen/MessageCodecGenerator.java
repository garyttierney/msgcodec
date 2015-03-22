package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.message.MessageCodecFactory;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;
import org.apollo.extension.releasegen.message.node.AttributeType;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.parser.MessageParser;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageCodecGenerator {
    private final MessageParser messageParser = Parboiled.createParser(MessageParser.class);
    private final MessageCodecFactory messageCodecFactory;

    private final Set<Path> messageConfigPaths = new HashSet<>();

    public MessageCodecGenerator(MessageCodecFactory messageCodecFactory, Path messageConfigPath) throws IOException {
        MessageConfigFileVisitor configFileVisitor = new MessageConfigFileVisitor();
        Files.walkFileTree(messageConfigPath, configFileVisitor);

        this.messageConfigPaths.addAll(configFileVisitor.getMessageConfigPaths());
        this.messageCodecFactory = messageCodecFactory;
    }

    public MessageCodec generate(int version) throws MessageCodecGeneratorException {
        ParseRunner<MessageNode> parseRunner = new RecoveringParseRunner<>(messageParser.messageNode());

        final Map<Integer, MessageDeserializer> deserializerMap = new HashMap<>();
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

                String type = node.getAttribute("type").getValue();
                int opcode = Integer.valueOf(node.getAttribute("opcode").getValue());

                try {
                    if (type.equalsIgnoreCase("outgoing")) {
                        serializerMap.put(Class.forName(node.getIdentifier()), messageCodecFactory.createSerializer(node));
                    } else if (type.equalsIgnoreCase("incoming")) {
                        deserializerMap.put(opcode, messageCodecFactory.createDeserializer(node));
                    } else {
                        throw new MessageCodecGeneratorException("Got an unknown type \"" + type + "\" for message \"" + node.getIdentifier() + "\"");
                    }
                } catch (Exception ex) {
                    throw new MessageCodecGeneratorException("Error occurred when creating serializer or deserializer for \"" + node.getIdentifier() + "\"", ex);
                }

            } catch (IOException e) {
                throw new MessageCodecGeneratorException("Unable to read message config file \"" + configPath.toString() + "\"", e);
            }
        }

        return new MessageCodec(version, deserializerMap, serializerMap);
    }
}
