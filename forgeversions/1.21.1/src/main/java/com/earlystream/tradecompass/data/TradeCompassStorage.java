package com.earlystream.tradecompass.data;

import com.earlystream.tradecompass.config.TradeCompassSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TradeCompassStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("FindMyVillager/Data");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path tradeCompassDirectory;
    private final Path configFile;
    private final Path worldsDirectory;
    private final Path exportsDirectory;
    private final Path importsDirectory;
    private final Path backupsDirectory;

    public TradeCompassStorage(Path configDirectory) {
        this.tradeCompassDirectory = configDirectory.resolve("tradecompass");
        this.configFile = tradeCompassDirectory.resolve("config.json");
        this.worldsDirectory = tradeCompassDirectory.resolve("worlds");
        this.exportsDirectory = tradeCompassDirectory.resolve("exports");
        this.importsDirectory = tradeCompassDirectory.resolve("imports");
        this.backupsDirectory = tradeCompassDirectory.resolve("backups");
    }

    public TradeCompassSettings loadSettings() {
        if (!Files.exists(configFile)) {
            return new TradeCompassSettings();
        }
        try (Reader reader = Files.newBufferedReader(configFile)) {
            TradeCompassSettings settings = gson.fromJson(reader, TradeCompassSettings.class);
            return settings == null ? new TradeCompassSettings() : settings;
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Failed to load Find My Villager settings from {}", configFile, exception);
            return new TradeCompassSettings();
        }
    }

    public void saveSettings(TradeCompassSettings settings) {
        if (settings == null) {
            return;
        }
        try {
            SafeJsonFileWriter.write(gson, configFile, settings);
        } catch (IOException exception) {
            LOGGER.warn("Failed to save Find My Villager settings to {}", configFile, exception);
        }
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
            LOGGER.warn("Failed to load Find My Villager database from {}", path, exception);
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
        } catch (IOException exception) {
            LOGGER.warn("Failed to scan fallback Find My Villager databases in {}", worldsDirectory, exception);
        }
        return database;
    }

    public void save(WorldTradeDatabase database) {
        if (database == null || database.worldKey().isBlank()) {
            return;
        }
        try {
            SafeJsonFileWriter.write(gson, pathFor(database.worldKey()), database);
        } catch (IOException exception) {
            LOGGER.warn("Failed to save Find My Villager database {}", database.worldKey(), exception);
        }
    }

    public Path pathFor(String worldKey) {
        return worldsDirectory.resolve(worldKey + ".json");
    }

    public Path tradeCompassDirectory() {
        return tradeCompassDirectory;
    }

    public Path worldsDirectory() {
        return worldsDirectory;
    }

    public Path exportsDirectory() {
        return exportsDirectory;
    }

    public Path importsDirectory() {
        return importsDirectory;
    }

    public Path backupsDirectory() {
        return backupsDirectory;
    }

    public int knownDatabaseCount() {
        if (!Files.isDirectory(worldsDirectory)) {
            return 0;
        }
        int count = 0;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(worldsDirectory, "*.json")) {
            for (Path ignored : files) {
                count++;
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to count Find My Villager databases in {}", worldsDirectory, exception);
        }
        return count;
    }

    public List<Path> databaseFiles() {
        List<Path> files = new ArrayList<>();
        if (!Files.isDirectory(worldsDirectory)) {
            return files;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worldsDirectory, "*.json")) {
            for (Path file : stream) {
                files.add(file);
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to list Find My Villager databases in {}", worldsDirectory, exception);
        }
        return files;
    }
}
