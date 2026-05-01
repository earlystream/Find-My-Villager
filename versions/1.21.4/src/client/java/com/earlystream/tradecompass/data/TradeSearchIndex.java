package com.earlystream.tradecompass.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class TradeSearchIndex {
    private TradeSearchIndex() {
    }

    public static List<SearchResult> search(WorldTradeDatabase database, String query, String currentDimension, double playerX, double playerY, double playerZ) {
        List<SearchResult> results = new ArrayList<>();
        for (GroupedSearchResult group : searchGrouped(database, query, currentDimension, playerX, playerY, playerZ)) {
            for (TradeOfferRecord offer : group.matchingOffers()) {
                results.add(new SearchResult(group.merchant(), offer, group.distance()));
            }
        }
        return results;
    }

    public static List<GroupedSearchResult> searchGrouped(WorldTradeDatabase database, String query, String currentDimension, double playerX, double playerY, double playerZ) {
        return searchGrouped(database, query, currentDimension, playerX, playerY, playerZ, true);
    }

    public static List<GroupedSearchResult> searchGrouped(WorldTradeDatabase database, String query, String currentDimension, double playerX, double playerY, double playerZ, boolean includeVillagerNames) {
        String normalized = normalize(query);
        List<GroupedSearchResult> results = new ArrayList<>();
        for (MerchantRecord merchant : database.merchants()) {
            boolean merchantMatches = normalized.isBlank() || merchant.matches(normalized, includeVillagerNames);
            List<TradeOfferRecord> matchingOffers = new ArrayList<>();
            for (TradeOfferRecord offer : merchant.offers()) {
                if (merchantMatches || offer.matches(normalized)) {
                    matchingOffers.add(offer);
                }
            }
            if (!matchingOffers.isEmpty()) {
                results.add(new GroupedSearchResult(merchant, matchingOffers, distance(merchant, currentDimension, playerX, playerY, playerZ)));
            }
        }
        Comparator<GroupedSearchResult> comparator = Comparator
                .comparing((GroupedSearchResult result) -> !result.merchant().favorite())
                .thenComparingDouble(result -> result.distance() < 0 ? Double.MAX_VALUE : result.distance())
                .thenComparing(result -> lowestCurrentPrice(result.matchingOffers()));
        if ("cheap".equals(normalized) || "cheapest".equals(normalized) || normalized.contains("cheap")) {
            comparator = Comparator.comparing((GroupedSearchResult result) -> !result.merchant().favorite())
                    .thenComparingInt(result -> lowestCurrentPrice(result.matchingOffers()))
                    .thenComparingDouble(result -> result.distance() < 0 ? Double.MAX_VALUE : result.distance());
        }
        results.sort(comparator);
        return results;
    }

    public static String normalize(String query) {
        return query == null ? "" : query.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
    }

    private static double distance(MerchantRecord merchant, String currentDimension, double playerX, double playerY, double playerZ) {
        if (merchant.unknownPosition() || currentDimension == null || !currentDimension.equals(merchant.dimension())) {
            return -1.0D;
        }
        double dx = merchant.x() - playerX;
        double dy = merchant.y() - playerY;
        double dz = merchant.z() - playerZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static int lowestCurrentPrice(List<TradeOfferRecord> offers) {
        int lowest = Integer.MAX_VALUE;
        for (TradeOfferRecord offer : offers) {
            lowest = Math.min(lowest, offer.currentPrice());
        }
        return lowest == Integer.MAX_VALUE ? 0 : lowest;
    }
}
