package com.earlystream.tradecompass.capture;

public record PendingMerchantInteraction(
        String entityUuid,
        String entityTypeId,
        String entityType,
        String professionId,
        String profession,
        String detectedName,
        String level,
        int levelNumber,
        int villagerXp,
        int nextLevelXp,
        String dimension,
        double x,
        double y,
        double z,
        long timestampMillis
) {
    public boolean expired(long nowMillis, long ttlMillis) {
        return nowMillis - timestampMillis > ttlMillis;
    }

    public String merchantKey(String worldKey) {
        if (entityUuid == null || entityUuid.isBlank()) {
            return "unknown-" + timestampMillis;
        }
        return worldKey + "-" + entityUuid;
    }
}
