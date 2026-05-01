package com.earlystream.tradecompass.data;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DatabaseBackupService {
    private static final int BACKUPS_TO_KEEP_PER_DATABASE = 10;
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault());

    private DatabaseBackupService() {
    }

    public static Path backupNow(TradeCompassStorage storage, WorldTradeDatabase database, String modVersion) throws IOException {
        String databaseId = database == null ? "" : database.worldKey();
        String prefix = DatabaseExportService.safeDatabaseId(databaseId) + "-backup-";
        Path target = storage.backupsDirectory().resolve(prefix + FILE_TIMESTAMP.format(Instant.now()) + ".json");
        DatabaseExportService.exportTo(storage, database, modVersion, target);
        rotateBackups(storage, prefix);
        return target;
    }

    public static Path lastBackup(TradeCompassStorage storage, String databaseId) {
        List<Path> backups = backupsFor(storage, DatabaseExportService.safeDatabaseId(databaseId) + "-backup-");
        return backups.isEmpty() ? null : backups.get(0);
    }

    private static void rotateBackups(TradeCompassStorage storage, String prefix) throws IOException {
        List<Path> backups = backupsFor(storage, prefix);
        for (int i = BACKUPS_TO_KEEP_PER_DATABASE; i < backups.size(); i++) {
            Files.deleteIfExists(backups.get(i));
        }
    }

    private static List<Path> backupsFor(TradeCompassStorage storage, String prefix) {
        List<Path> backups = new ArrayList<>();
        Path directory = storage.backupsDirectory();
        if (!Files.isDirectory(directory)) {
            return backups;
        }
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory, prefix + "*.json")) {
            for (Path file : files) {
                backups.add(file);
            }
        } catch (IOException ignored) {
        }
        backups.sort(Comparator.comparingLong(DatabaseBackupService::lastModified).reversed());
        return backups;
    }

    private static long lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException exception) {
            return 0L;
        }
    }
}
