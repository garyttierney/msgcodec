package org.apollo.extension.releasegen;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class MessageConfigFileVisitor extends SimpleFileVisitor<Path> {
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
