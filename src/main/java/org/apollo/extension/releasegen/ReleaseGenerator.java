package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.cgen.MessageDeserializerClassWriter;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;
import org.apollo.extension.releasegen.message.node.AttributeType;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.parser.MessageParser;
import org.objectweb.asm.ClassWriter;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReleaseGenerator {
    private final ReleaseGeneratorClassLoader classLoader = new ReleaseGeneratorClassLoader(ReleaseGenerator.class.getClassLoader());
    private final MessageParser messageParser = Parboiled.createParser(MessageParser.class);
    private final Map<Integer, MessageDeserializer> deserializerMap = new HashMap<>();
    private final Map<Integer, MessageSerializer> serializerMap = new HashMap<>();
    private final Set<Path> messageConfigPaths;

    public ReleaseGenerator(Path messageConfigPath) throws IOException {
        MessageConfigFileVisitor configFileVisitor = new MessageConfigFileVisitor();
        Files.walkFileTree(messageConfigPath, configFileVisitor);

        this.messageConfigPaths = configFileVisitor.getMessageConfigPaths();
    }

    public void init() throws ReleaseGeneratorException {
        ParseRunner<MessageNode> parseRunner = new RecoveringParseRunner<>(messageParser.messageNode());
        for(Path configPath : messageConfigPaths) {
            try {
                ParsingResult<MessageNode> messageParsingResult = parseRunner.run((CharSequence) new String(Files.readAllBytes(configPath)));
                MessageNode node = messageParsingResult.resultValue;
                if (!node.hasAttribute("type")) {
                    throw new ReleaseGeneratorException("Message configuration file for \"" + node.getIdentifier() + "\" does not have a \"type\" attribute");
                }

                if (!node.hasAttribute("opcode") || node.getAttribute("opcode").getType() != AttributeType.NUMBER_LITERAL) {
                    throw new ReleaseGeneratorException("Message configuration file for \"" + node.getIdentifier() + "\" does not have a valid \"opcode\" attribute");
                }

                String type = node.getAttribute("type").getValue();
                int opcode = Integer.valueOf(node.getAttribute("opcode").getValue());

                try {
                    if (type.equalsIgnoreCase("outgoing")) {
                        serializerMap.put(opcode, createMessageSerializer(node));
                    } else if (type.equalsIgnoreCase("incoming")) {
                        deserializerMap.put(opcode, createMessageDeserializer(node));
                    } else {
                        throw new ReleaseGeneratorException("Got an unknown type \"" + type + "\" for message \"" + node.getIdentifier() + "\"");
                    }
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new ReleaseGeneratorException("Error occurred when creating serializer or deserializer for \"" + node.getIdentifier() + "\"", ex);
                }

            } catch (IOException e) {
                throw new ReleaseGeneratorException("Unable to read message config file \"" + configPath.toString() + "\"", e);
            }
        }
    }

    public MessageSerializer createMessageSerializer(MessageNode node) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//        MessageNodeVisitor visitor = new MessageSerializerVisitor(cw);

//        node.accept(visitor);
        return null;
    }

    public MessageDeserializer createMessageDeserializer(MessageNode node) throws IllegalAccessException, InstantiationException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        String deserializerClassName = node.getIdentifier() + "_Deserializer";
        node.accept(new MessageDeserializerClassWriter(deserializerClassName, cw));

        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();
        Class<?> clazz = classLoader.defineClassProxy(deserializerClassName, classBytes, 0, classBytes.length);

        return (MessageDeserializer) clazz.newInstance();
    }

}
