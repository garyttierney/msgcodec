package sfix.msgcodec.message;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import sfix.msgcodec.message.node.MessageNode;
import sfix.msgcodec.message.parser.MessageParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultMessageLoader extends AbstractFileMessageLoader {
    public DefaultMessageLoader(Path configurationPath) throws IOException {
        super(configurationPath);
    }

    @Override
    public Collection<MessageNode> load() throws MessageLoaderException {
        MessageParser messageParser = Parboiled.createParser(MessageParser.class);
        ParseRunner<MessageNode> parseRunner = new RecoveringParseRunner<>(messageParser.messageNode());

        Set<MessageNode> messageNodeSet = new HashSet<>();
        for (Path configPath : messageConfigPaths) {
            try {
                ParsingResult<MessageNode> messageParsingResult = parseRunner.run((CharSequence) new String(Files.readAllBytes(configPath)));
                messageNodeSet.add(messageParsingResult.resultValue);
            } catch (IOException e) {
                throw new MessageLoaderException("Failed to read MessageNode configuration file", e);
            }

        }

        return messageNodeSet;
    }
}
