package com.earlystream.tradecompass.data;

public record MerchantIdentity(
        String merchantKey,
        String entityUuid,
        String entityType,
        String profession,
        String level
) {
    public static MerchantIdentity unknown(String worldKey) {
        return new MerchantIdentity("unknown-" + System.currentTimeMillis(), "", "Unknown merchant", "Unknown", "");
    }
}
