package org.apollo.extension.releasegen;

import org.apollo.extension.releasegen.cgen.MessageDeserializerClassWriter;
import org.apollo.extension.releasegen.cgen.MethodReferenceResolver;
import org.apollo.extension.releasegen.message.MessageDeserializer;
import org.apollo.extension.releasegen.message.MessageSerializer;
import org.apollo.extension.releasegen.message.node.AttributeType;
import org.apollo.extension.releasegen.message.node.MessageNode;
import org.apollo.extension.releasegen.message.node.MessageNodeVisitorException;
import org.apollo.extension.releasegen.message.parser.MessageParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ReleaseGenerator {
    private final ReleaseGeneratorClassLoader classLoader = new ReleaseGeneratorClassLoader(ReleaseGenerator.class.getClassLoader());
    private final MessageParser messageParser = Parboiled.createParser(MessageParser.class);
    private final Map<Integer, MessageDeserializer> deserializerMap = new HashMap<>();
    private final Map<Integer, MessageSerializer> serializerMap = new HashMap<>();

    private final Set<Path> messageConfigPaths = new HashSet<>();
    private final MethodReferenceResolver methodReferenceResolver;

    public ReleaseGenerator(Path messageConfigPath, MethodReferenceResolver methodReferenceResolver) throws IOException {
        MessageConfigFileVisitor configFileVisitor = new MessageConfigFileVisitor();
        Files.walkFileTree(messageConfigPath, configFileVisitor);

        this.messageConfigPaths.addAll(configFileVisitor.getMessageConfigPaths());
        this.methodReferenceResolver = methodReferenceResolver;
    }

    public ReleaseGenerator(MethodReferenceResolver methodReferenceResolver) {
        this.methodReferenceResolver = methodReferenceResolver;
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
                } catch (Exception ex) {
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


    public static String insnToString(AbstractInsnNode insn){
        insn.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

private static Printer printer = new Textifier();
private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    public MessageDeserializer createMessageDeserializer(MessageNode node) throws IllegalAccessException, InstantiationException, MessageNodeVisitorException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        String deserializerClassName = node.getIdentifier() + "Deserializer";
        {
            node.accept(new MessageDeserializerClassWriter(deserializerClassName, cw, methodReferenceResolver));
        }
        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();

        ClassReader reader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode,0);
        @SuppressWarnings("unchecked")
        final List<MethodNode> methods = classNode.methods;
        for(MethodNode m: methods){
            InsnList inList = m.instructions;
            System.out.println(m.name);
            for(int i = 0; i< inList.size(); i++){
                System.err.print(insnToString(inList.get(i)));
            }
        }

        Class<?> clazz = classLoader.defineClassProxy(deserializerClassName, classBytes, 0, classBytes.length);

        return (MessageDeserializer) clazz.newInstance();
    }

    public MessageDeserializer getMessageDeserializer(int opcode) {
        return deserializerMap.get(opcode);
    }

    public MessageSerializer getMessageSerializer(int opcode) {
        return serializerMap.get(opcode);
    }

}
