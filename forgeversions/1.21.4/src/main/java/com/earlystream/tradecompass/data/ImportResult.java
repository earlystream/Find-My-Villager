package com.earlystream.tradecompass.data;

import java.nio.file.Path;

public record ImportResult(
        WorldTradeDatabase database,
        Path backupPath,
        int importedVillagers,
        int addedVillagers,
        int mergedVillagers,
        int addedTrades
) {
}
