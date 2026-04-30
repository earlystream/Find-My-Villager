package com.earlystream.tradecompass.config;

public final class TradeCompassConfig {
    public static final String MOD_ID = "tradecompass";
    public static final String MOD_NAME = "Find My Villager";
    public static final long PENDING_INTERACTION_TTL_MILLIS = 4_000L;
    public static final long RECENT_SCAN_MILLIS = 10L * 60L * 1_000L;
    public static final long STALE_SCAN_MILLIS = 7L * 24L * 60L * 60L * 1_000L;

    private TradeCompassConfig() {
    }
}
