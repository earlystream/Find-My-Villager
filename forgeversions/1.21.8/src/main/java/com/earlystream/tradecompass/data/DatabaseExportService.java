package com.earlystream.tradecompass.data;

import com.earlystream.tradecompass.config.TradeCompassConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DatabaseExportService {
    public static final int SCHEMA_VERSION = 1;
    public static final String EXPORT_KIND = "find-my-villager-database";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault());

    private DatabaseExportService() {
    }

    public static Path exportCurrent(TradeCompassStorage storage, WorldTradeDatabase database, String modVersion) throws IOException {
        String timestamp = FILE_TIMESTAMP.format(Instant.now());
        Path target = storage.exportsDirectory().resolve(safeDatabaseId(database.worldKey()) + "-export-" + timestamp + ".json");
        exportTo(storage, database, modVersion, target);
        return target;
    }

    public static void exportTo(TradeCompassStorage storage, WorldTradeDatabase database, String modVersion, Path target) throws IOException {
        DatabaseExport export = createExport(database, modVersion);
        SafeJsonFileWriter.write(GSON, target, export);
    }

    public static DatabaseExport createExport(WorldTradeDatabase database, String modVersion) {
        DatabaseStats stats = DatabaseStats.from(null, database);
        DatabaseExport export = new DatabaseExport();
        export.schemaVersion = SCHEMA_VERSION;
        export.kind = EXPORT_KIND;
        export.exportedAt = Instant.now().toString();
        export.exportedAtEpochMillis = System.currentTimeMillis();
        export.modId = TradeCompassConfig.MOD_ID;
        export.modName = TradeCompassConfig.MOD_NAME;
        export.modVersion = modVersion == null || modVersion.isBlank() ? "unknown" : modVersion;
        export.databaseId = database == null ? "" : database.worldKey();
        export.villagerCount = stats.villagerCount();
        export.tradeCount = stats.tradeCount();
        export.database = database == null ? new WorldTradeDatabase() : database;
        return export;
    }

    public static String safeDatabaseId(String databaseId) {
        String value = databaseId == null || databaseId.isBlank() ? "unknown" : databaseId;
        String safe = value.toLowerCase().replaceAll("[^a-z0-9._-]+", "-");
        return safe.isBlank() ? "unknown" : safe;
    }

    public static final class DatabaseExport {
        public int schemaVersion;
        public String kind;
        public String exportedAt;
        public long exportedAtEpochMillis;
        public String modId;
        public String modName;
        public String modVersion;
        public String databaseId;
        public int villagerCount;
        public int tradeCount;
        public WorldTradeDatabase database;
    }
}
