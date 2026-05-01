package com.earlystream.tradecompass.data;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public final class SafeJsonFileWriter {
    private SafeJsonFileWriter() {
    }

    public static void write(Gson gson, Path target, Object value) throws IOException {
        writeText(target, gson.toJson(value));
    }

    public static void writeText(Path target, String content) throws IOException {
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temp = target.resolveSibling(target.getFileName() + ".tmp-" + System.nanoTime());
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try {
            try (FileChannel channel = FileChannel.open(
                    temp,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            )) {
                channel.write(ByteBuffer.wrap(bytes));
                channel.force(true);
            }
            moveIntoPlace(temp, target);
        } catch (IOException exception) {
            Files.deleteIfExists(temp);
            throw exception;
        }
    }

    private static void moveIntoPlace(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException atomicMoveFailed) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
