package sfix.msgcodec.message;

import org.yaml.snakeyaml.Yaml;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.parser.YamlMessageParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YamlMessageLoader extends AbstractFileMessageLoader {
    private final YamlMessageParser messageParser;

    //@todo - no no no no !
    private Yaml yaml = new Yaml();

    public YamlMessageLoader(YamlMessageParser messageParser,  Path configurationPath) throws IOException {
        super(configurationPath);

        this.messageParser = messageParser;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<MessageNode> load() throws MessageLoaderException {
        Set<MessageNode> messageNodeSet = new HashSet<>();

        for(Path messageConfigPath : messageConfigPaths) {
            try (InputStream is = Files.newInputStream(messageConfigPath)) {
                for(Object yamlObject : yaml.loadAll(is)) {
                    if (!(yamlObject instanceof Map)) {
                        throw new MessageLoaderException("Found a Yaml object which wasn't a message codec configuration");
                    }

                    messageNodeSet.add(messageParser.parseYamlObject((Map<String, Object>) yamlObject));
                }

                yaml.load(Files.newInputStream(messageConfigPath));
            } catch (IOException e) {
                throw new MessageLoaderException("Failed to load message codec yaml file", e);
            }
        }

        return messageNodeSet;
    }
}
