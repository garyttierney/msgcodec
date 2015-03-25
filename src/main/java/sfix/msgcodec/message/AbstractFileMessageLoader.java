package sfix.msgcodec.message;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract MessageLoader which takes a <code>Path</code> to walk and find configuration files in.
 */
public abstract class AbstractFileMessageLoader implements MessageLoader {

    /**
     * A set of message configuration files to load.
     */
    protected final Set<Path> messageConfigPaths = new HashSet<>();

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


    public AbstractFileMessageLoader(Path configurationPath) throws IOException {
        MessageConfigFileVisitor configFileVisitor = new MessageConfigFileVisitor();
        Files.walkFileTree(configurationPath, configFileVisitor);

        this.messageConfigPaths.addAll(configFileVisitor.getMessageConfigPaths());
    }
}
