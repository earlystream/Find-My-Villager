package com.earlystream.tradecompass.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class DatabaseImportService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private DatabaseImportService() {
    }

    public static ImportResult importFrom(
            Path source,
            TradeCompassStorage storage,
            WorldTradeDatabase currentDatabase,
            String activeDatabaseId,
            String modVersion,
            ImportConflictStrategy strategy
    ) throws IOException, ImportValidationException {
        WorldTradeDatabase imported = readAndValidate(source, activeDatabaseId);
        Path backupPath = DatabaseBackupService.backupNow(storage, currentDatabase, modVersion);
        ImportConflictResolver.Result result = ImportConflictResolver.resolve(
                currentDatabase,
                imported,
                activeDatabaseId,
                strategy == null ? ImportConflictStrategy.MERGE : strategy
        );
        return new ImportResult(
                result.database(),
                backupPath,
                imported.size(),
                result.addedVillagers(),
                result.mergedVillagers(),
                result.addedTrades()
        );
    }

    public static WorldTradeDatabase readAndValidate(Path source, String activeDatabaseId) throws IOException, ImportValidationException {
        if (source == null || !Files.isRegularFile(source)) {
            throw new ImportValidationException("Import file does not exist.");
        }
        JsonElement root;
        try (Reader reader = Files.newBufferedReader(source)) {
            root = JsonParser.parseReader(reader);
        } catch (RuntimeException exception) {
            throw new ImportValidationException("Import file is not valid JSON.");
        }
        if (root == null || !root.isJsonObject()) {
            throw new ImportValidationException("Import JSON must be an object.");
        }
        JsonObject object = root.getAsJsonObject();
        WorldTradeDatabase database;
        if (object.has("database")) {
            validateExportWrapper(object);
            database = GSON.fromJson(object.get("database"), WorldTradeDatabase.class);
        } else if (object.has("worldKey") && object.has("merchants")) {
            database = GSON.fromJson(object, WorldTradeDatabase.class);
        } else {
            throw new ImportValidationException("Import JSON is not a Find My Villager database export.");
        }
        if (database == null) {
            throw new ImportValidationException("Import JSON does not contain a database.");
        }
        String targetDatabaseId = activeDatabaseId == null ? "" : activeDatabaseId;
        database.worldKey(targetDatabaseId);
        WorldTradeDatabase sanitized = new WorldTradeDatabase();
        sanitized.worldKey(targetDatabaseId);
        int fallbackIndex = 0;
        for (MerchantRecord merchant : database.merchants()) {
            MerchantRecord clean = ImportConflictResolver.normalizedCopy(merchant, targetDatabaseId);
            if (clean == null) {
                continue;
            }
            if (clean.merchantKey().isBlank()) {
                clean.merchantKey("imported-" + System.currentTimeMillis() + "-" + fallbackIndex++);
            }
            sanitized.upsert(clean);
        }
        return sanitized;
    }

    public static Optional<Path> latestFallbackImport(TradeCompassStorage storage) {
        Path directory = storage.importsDirectory();
        if (!Files.isDirectory(directory)) {
            return Optional.empty();
        }
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory, "*.json")) {
            Path newest = null;
            for (Path file : files) {
                if (newest == null || lastModified(file) > lastModified(newest)) {
                    newest = file;
                }
            }
            return Optional.ofNullable(newest);
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private static void validateExportWrapper(JsonObject object) throws ImportValidationException {
        if (object.has("kind") && !DatabaseExportService.EXPORT_KIND.equals(object.get("kind").getAsString())) {
            throw new ImportValidationException("Import JSON is for a different export type.");
        }
        if (object.has("schemaVersion")) {
            int schemaVersion;
            try {
                schemaVersion = object.get("schemaVersion").getAsInt();
            } catch (RuntimeException exception) {
                throw new ImportValidationException("Import schemaVersion is invalid.");
            }
            if (schemaVersion > DatabaseExportService.SCHEMA_VERSION) {
                throw new ImportValidationException("Import JSON uses a newer unsupported schema.");
            }
        }
    }

    private static long lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException exception) {
            return 0L;
        }
    }

    public static final class ImportValidationException extends Exception {
        public ImportValidationException(String message) {
            super(message);
        }
    }
}
