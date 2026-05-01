package com.earlystream.tradecompass.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ImportConflictResolver {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private ImportConflictResolver() {
    }

    public static Result resolve(
            WorldTradeDatabase current,
            WorldTradeDatabase imported,
            String activeDatabaseId,
            ImportConflictStrategy strategy
    ) {
        WorldTradeDatabase result = copyDatabase(current, activeDatabaseId);
        int addedVillagers = 0;
        int mergedVillagers = 0;
        int addedTrades = 0;
        for (MerchantRecord importedMerchant : imported.merchants()) {
            MerchantRecord incoming = normalizedCopy(importedMerchant, activeDatabaseId);
            if (incoming == null) {
                continue;
            }
            MerchantRecord existing = findConflict(result, incoming);
            if (existing == null) {
                result.upsert(incoming);
                addedVillagers++;
                addedTrades += incoming.offers().size();
                continue;
            }
            switch (strategy) {
                case KEEP_EXISTING -> {
                }
                case REPLACE_EXISTING -> {
                    result.delete(existing.merchantKey());
                    result.upsert(incoming);
                    mergedVillagers++;
                    addedTrades += incoming.offers().size();
                }
                case IMPORT_AS_DUPLICATE -> {
                    incoming.merchantKey(duplicateKey(result, incoming.merchantKey()));
                    result.upsert(incoming);
                    addedVillagers++;
                    addedTrades += incoming.offers().size();
                }
                case MERGE -> {
                    MergeResult merge = merge(existing, incoming);
                    result.delete(existing.merchantKey());
                    result.upsert(merge.merchant());
                    mergedVillagers++;
                    addedTrades += merge.addedTrades();
                }
            }
        }
        result.worldKey(activeDatabaseId);
        return new Result(result, addedVillagers, mergedVillagers, addedTrades);
    }

    public static MerchantRecord normalizedCopy(MerchantRecord merchant, String activeDatabaseId) {
        if (merchant == null) {
            return null;
        }
        MerchantRecord copy = copyMerchant(merchant);
        copy.worldKey(activeDatabaseId);
        copy.merchantKey(clean(copy.merchantKey()));
        copy.entityUuid(clean(copy.entityUuid()));
        copy.entityTypeId(clean(copy.entityTypeId()));
        copy.entityType(clean(copy.entityType()));
        copy.professionId(clean(copy.professionId()));
        copy.profession(clean(copy.profession()));
        copy.detectedName(copy.detectedName());
        copy.manualName(copy.manualName());
        copy.level(clean(copy.level()));
        copy.dimension(clean(copy.dimension()));
        List<TradeOfferRecord> offers = new ArrayList<>();
        for (TradeOfferRecord offer : copy.offers()) {
            if (offer == null) {
                continue;
            }
            offer.inputOne(offer.inputOne());
            offer.inputTwo(offer.inputTwo());
            offer.output(offer.output());
            offer.outputEnchantments(offer.outputEnchantments());
            offers.add(offer);
        }
        copy.offers(offers);
        return copy;
    }

    private static MergeResult merge(MerchantRecord existing, MerchantRecord incoming) {
        boolean incomingIsNewer = incoming.lastScannedEpochMillis() > existing.lastScannedEpochMillis();
        MerchantRecord merged = incomingIsNewer ? copyMerchant(incoming) : copyMerchant(existing);

        if (merged.merchantKey().isBlank()) {
            merged.merchantKey(firstNonBlank(existing.merchantKey(), incoming.merchantKey()));
        }
        merged.entityUuid(firstNonBlank(existing.entityUuid(), incoming.entityUuid()));
        merged.entityTypeId(firstNonBlank(incoming.entityTypeId(), existing.entityTypeId()));
        merged.entityType(firstUseful(incoming.entityType(), existing.entityType(), "Unknown merchant"));
        merged.professionId(firstNonBlank(incoming.professionId(), existing.professionId()));
        merged.profession(firstUseful(incoming.profession(), existing.profession(), "Unknown"));
        merged.detectedName(firstNonBlank(incoming.detectedName(), existing.detectedName()));
        merged.manualName(firstNonBlank(existing.manualName(), incoming.manualName()));
        merged.favorite(existing.favorite() || incoming.favorite());
        merged.level(firstNonBlank(incoming.level(), existing.level()));
        merged.levelNumber(incoming.levelNumber() > 0 ? incoming.levelNumber() : existing.levelNumber());
        merged.villagerXp(incomingIsNewer ? incoming.villagerXp() : existing.villagerXp());
        merged.nextLevelXp(incomingIsNewer ? incoming.nextLevelXp() : existing.nextLevelXp());
        merged.worldKey(firstNonBlank(existing.worldKey(), incoming.worldKey()));
        merged.dimension(firstNonBlank(incoming.dimension(), existing.dimension()));
        if (incomingIsNewer && !incoming.unknownPosition()) {
            merged.x(incoming.x());
            merged.y(incoming.y());
            merged.z(incoming.z());
            merged.unknownPosition(false);
        } else if (!existing.unknownPosition()) {
            merged.x(existing.x());
            merged.y(existing.y());
            merged.z(existing.z());
            merged.unknownPosition(false);
        } else {
            merged.unknownPosition(incoming.unknownPosition());
        }
        merged.lastScannedEpochMillis(Math.max(existing.lastScannedEpochMillis(), incoming.lastScannedEpochMillis()));

        List<TradeOfferRecord> offers = new ArrayList<>();
        for (TradeOfferRecord offer : existing.offers()) {
            offers.add(copyOffer(offer));
        }
        int addedTrades = 0;
        for (TradeOfferRecord offer : incoming.offers()) {
            String key = tradeIdentityKey(offer);
            int existingIndex = indexOfOffer(offers, key);
            if (existingIndex < 0) {
                offers.add(copyOffer(offer));
                addedTrades++;
            } else if (incomingIsNewer) {
                offers.set(existingIndex, copyOffer(offer));
            }
        }
        merged.offers(offers);
        return new MergeResult(merged, addedTrades);
    }

    private static MerchantRecord findConflict(WorldTradeDatabase database, MerchantRecord incoming) {
        if (!incoming.merchantKey().isBlank()) {
            MerchantRecord byKey = database.merchant(incoming.merchantKey());
            if (byKey != null) {
                return byKey;
            }
        }
        if (!incoming.entityUuid().isBlank()) {
            for (MerchantRecord merchant : database.merchants()) {
                if (incoming.entityUuid().equals(merchant.entityUuid())) {
                    return merchant;
                }
            }
        }
        if (!incoming.unknownPosition()) {
            for (MerchantRecord merchant : database.merchants()) {
                if (merchant.unknownPosition()) {
                    continue;
                }
                if (sameLocation(merchant, incoming)) {
                    return merchant;
                }
            }
        }
        return null;
    }

    private static boolean sameLocation(MerchantRecord left, MerchantRecord right) {
        return left.worldKey().equals(right.worldKey())
                && left.dimension().equals(right.dimension())
                && Math.round(left.x()) == Math.round(right.x())
                && Math.round(left.y()) == Math.round(right.y())
                && Math.round(left.z()) == Math.round(right.z());
    }

    private static String duplicateKey(WorldTradeDatabase database, String baseKey) {
        String base = baseKey == null || baseKey.isBlank() ? "imported" : baseKey;
        String candidate = base + "-imported";
        int index = 2;
        while (database.merchant(candidate) != null) {
            candidate = base + "-imported-" + index++;
        }
        return candidate;
    }

    private static int indexOfOffer(List<TradeOfferRecord> offers, String key) {
        for (int i = 0; i < offers.size(); i++) {
            if (tradeIdentityKey(offers.get(i)).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private static String tradeIdentityKey(TradeOfferRecord offer) {
        if (offer == null) {
            return "";
        }
        return stackKey(offer.inputOne()) + "|"
                + stackKey(offer.inputTwo()) + "|"
                + stackKey(offer.output()) + "|"
                + String.join(",", offer.outputEnchantments()).toLowerCase(Locale.ROOT) + "|"
                + offer.basePrice() + "|"
                + offer.maxUses();
    }

    private static String stackKey(ItemStackRecord stack) {
        if (stack == null) {
            return "minecraft:air:0";
        }
        return clean(stack.itemId()) + ":" + stack.count() + ":" + clean(stack.name()).toLowerCase(Locale.ROOT);
    }

    private static WorldTradeDatabase copyDatabase(WorldTradeDatabase database, String activeDatabaseId) {
        WorldTradeDatabase copy = new WorldTradeDatabase();
        copy.worldKey(activeDatabaseId);
        if (database == null) {
            return copy;
        }
        for (MerchantRecord merchant : database.merchants()) {
            MerchantRecord merchantCopy = normalizedCopy(merchant, activeDatabaseId);
            if (merchantCopy != null) {
                copy.upsert(merchantCopy);
            }
        }
        return copy;
    }

    private static MerchantRecord copyMerchant(MerchantRecord merchant) {
        return GSON.fromJson(GSON.toJson(merchant), MerchantRecord.class);
    }

    private static TradeOfferRecord copyOffer(TradeOfferRecord offer) {
        return GSON.fromJson(GSON.toJson(offer), TradeOfferRecord.class);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? clean(fallback) : preferred;
    }

    private static String firstUseful(String preferred, String fallback, String unknownValue) {
        if (preferred == null || preferred.isBlank() || preferred.equalsIgnoreCase(unknownValue)) {
            return firstNonBlank(fallback, unknownValue);
        }
        return preferred;
    }

    private static String clean(String value) {
        return value == null ? "" : value;
    }

    private record MergeResult(MerchantRecord merchant, int addedTrades) {
    }

    public record Result(WorldTradeDatabase database, int addedVillagers, int mergedVillagers, int addedTrades) {
    }
}
