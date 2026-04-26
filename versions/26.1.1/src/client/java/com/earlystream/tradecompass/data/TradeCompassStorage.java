package com.earlystream.tradecompass.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TradeCompassStorage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path worldsDirectory;

    public TradeCompassStorage(Path configDirectory) {
        this.worldsDirectory = configDirectory.resolve("tradecompass").resolve("worlds");
    }

    public WorldTradeDatabase load(String worldKey) {
        Path path = pathFor(worldKey);
        if (!Files.exists(path)) {
            WorldTradeDatabase database = new WorldTradeDatabase();
            database.worldKey(worldKey);
            return database;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            WorldTradeDatabase database = gson.fromJson(reader, WorldTradeDatabase.class);
            if (database == null) {
                database = new WorldTradeDatabase();
            }
            database.worldKey(worldKey);
            return database;
        } catch (IOException | RuntimeException exception) {
            WorldTradeDatabase database = new WorldTradeDatabase();
            database.worldKey(worldKey);
            return database;
        }
    }

    public WorldTradeDatabase loadWithFallback(String worldKey) {
        WorldTradeDatabase database = load(worldKey);
        if (database.size() > 0 || !Files.isDirectory(worldsDirectory)) {
            return database;
        }
        try (DirectoryStream<Path> files = Files.newDirectoryStream(worldsDirectory, "*.json")) {
            for (Path file : files) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    WorldTradeDatabase saved = gson.fromJson(reader, WorldTradeDatabase.class);
                    if (saved == null) {
                        continue;
                    }
                    for (MerchantRecord merchant : saved.merchants()) {
                        if (merchant != null && !merchant.offers().isEmpty()) {
                            database.upsert(merchant);
                        }
                    }
                } catch (IOException | RuntimeException ignored) {
                }
            }
            database.worldKey(worldKey);
        } catch (IOException ignored) {
        }
        return database;
    }

    public void save(WorldTradeDatabase database) {
        if (database == null || database.worldKey().isBlank()) {
            return;
        }
        try {
            Files.createDirectories(worldsDirectory);
            try (Writer writer = Files.newBufferedWriter(pathFor(database.worldKey()))) {
                gson.toJson(database, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public Path pathFor(String worldKey) {
        return worldsDirectory.resolve(worldKey + ".json");
    }
}
