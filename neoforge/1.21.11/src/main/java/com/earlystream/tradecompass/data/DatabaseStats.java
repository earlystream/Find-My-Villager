package com.earlystream.tradecompass.data;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public record DatabaseStats(
        String databaseId,
        int villagerCount,
        int tradeCount,
        int favoriteCount,
        int knownDatabaseCount,
        Path lastBackupPath,
        long lastBackupEpochMillis
) {
    public static DatabaseStats from(TradeCompassStorage storage, WorldTradeDatabase database) {
        String databaseId = database == null ? "" : database.worldKey();
        int villagers = 0;
        int trades = 0;
        int favorites = 0;
        if (database != null) {
            for (MerchantRecord merchant : database.merchants()) {
                if (merchant == null) {
                    continue;
                }
                villagers++;
                trades += merchant.offers().size();
                if (isFavorite(merchant)) {
                    favorites++;
                }
            }
        }
        int knownDatabases = storage == null ? 0 : storage.knownDatabaseCount();
        Path lastBackup = storage == null ? null : DatabaseBackupService.lastBackup(storage, databaseId);
        long lastBackupMillis = lastBackup == null ? 0L : lastModified(lastBackup);
        return new DatabaseStats(databaseId, villagers, trades, favorites, knownDatabases, lastBackup, lastBackupMillis);
    }

    private static boolean isFavorite(MerchantRecord merchant) {
        for (String methodName : new String[]{"favorite", "isFavorite"}) {
            try {
                Method method = merchant.getClass().getMethod(methodName);
                if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
                    Object value = method.invoke(merchant);
                    return Boolean.TRUE.equals(value);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return false;
    }

    private static long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (Exception exception) {
            return 0L;
        }
    }
}
