package com.earlystream.tradecompass.data;

import com.earlystream.tradecompass.config.TradeCompassConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MerchantRecord {
    private String merchantKey = "";
    private String entityUuid = "";
    private String entityTypeId = "";
    private String entityType = "Unknown merchant";
    private String professionId = "";
    private String profession = "Unknown";
    private String detectedName = "";
    private String manualName = "";
    private String level = "";
    private int levelNumber;
    private int villagerXp;
    private int nextLevelXp = -1;
    private String worldKey = "";
    private String dimension = "";
    private double x;
    private double y;
    private double z;
    private boolean unknownPosition = true;
    private long lastScannedEpochMillis;
    private List<TradeOfferRecord> offers = new ArrayList<>();

    public String merchantKey() {
        return merchantKey;
    }

    public void merchantKey(String merchantKey) {
        this.merchantKey = merchantKey == null ? "" : merchantKey;
    }

    public String entityUuid() {
        return entityUuid;
    }

    public void entityUuid(String entityUuid) {
        this.entityUuid = entityUuid == null ? "" : entityUuid;
    }

    public String entityTypeId() {
        return entityTypeId;
    }

    public void entityTypeId(String entityTypeId) {
        this.entityTypeId = entityTypeId == null ? "" : entityTypeId;
    }

    public String entityType() {
        return entityType;
    }

    public void entityType(String entityType) {
        this.entityType = blankDefault(entityType, "Unknown merchant");
    }

    public String professionId() {
        return professionId;
    }

    public void professionId(String professionId) {
        this.professionId = professionId == null ? "" : professionId;
    }

    public String profession() {
        return profession;
    }

    public void profession(String profession) {
        this.profession = blankDefault(profession, "Unknown");
    }

    public String detectedName() {
        return detectedName == null ? "" : detectedName;
    }

    public void detectedName(String detectedName) {
        this.detectedName = cleanName(detectedName);
    }

    public String manualName() {
        return manualName == null ? "" : manualName;
    }

    public void manualName(String manualName) {
        this.manualName = cleanName(manualName);
    }

    public String villagerName() {
        String manual = manualName();
        if (!manual.isBlank()) {
            return manual;
        }
        return detectedName();
    }

    public boolean hasVillagerName() {
        return !villagerName().isBlank();
    }

    public void copyManualNameFrom(MerchantRecord existing) {
        if (existing != null && !existing.manualName().isBlank()) {
            manualName(existing.manualName());
        }
    }

    public String level() {
        return level;
    }

    public void level(String level) {
        this.level = level == null ? "" : level;
    }

    public int levelNumber() {
        return levelNumber;
    }

    public void levelNumber(int levelNumber) {
        this.levelNumber = Math.max(0, Math.min(5, levelNumber));
        if (this.level.isBlank() && this.levelNumber > 0) {
            this.level = levelName(this.levelNumber);
        }
    }

    public int villagerXp() {
        return villagerXp;
    }

    public void villagerXp(int villagerXp) {
        this.villagerXp = Math.max(0, villagerXp);
    }

    public int nextLevelXp() {
        return nextLevelXp;
    }

    public void nextLevelXp(int nextLevelXp) {
        this.nextLevelXp = nextLevelXp;
    }

    public String worldKey() {
        return worldKey;
    }

    public void worldKey(String worldKey) {
        this.worldKey = worldKey == null ? "" : worldKey;
    }

    public String dimension() {
        return dimension;
    }

    public void dimension(String dimension) {
        this.dimension = dimension == null ? "" : dimension;
    }

    public double x() {
        return x;
    }

    public void x(double x) {
        this.x = x;
    }

    public double y() {
        return y;
    }

    public void y(double y) {
        this.y = y;
    }

    public double z() {
        return z;
    }

    public void z(double z) {
        this.z = z;
    }

    public boolean unknownPosition() {
        return unknownPosition;
    }

    public void unknownPosition(boolean unknownPosition) {
        this.unknownPosition = unknownPosition;
    }

    public long lastScannedEpochMillis() {
        return lastScannedEpochMillis;
    }

    public void lastScannedEpochMillis(long lastScannedEpochMillis) {
        this.lastScannedEpochMillis = lastScannedEpochMillis;
    }

    public List<TradeOfferRecord> offers() {
        if (offers == null) {
            offers = new ArrayList<>();
        }
        return offers;
    }

    public void offers(List<TradeOfferRecord> offers) {
        this.offers = offers == null ? new ArrayList<>() : new ArrayList<>(offers);
    }

    public String status(long nowMillis) {
        if (unknownPosition) {
            return "Unknown position";
        }
        long age = Math.max(0L, nowMillis - lastScannedEpochMillis);
        if (age <= TradeCompassConfig.RECENT_SCAN_MILLIS) {
            return "Fresh";
        }
        if (age >= TradeCompassConfig.STALE_SCAN_MILLIS) {
            return "Stale";
        }
        return "Last seen recently";
    }

    public boolean matches(String normalizedQuery) {
        return matches(normalizedQuery, true);
    }

    public boolean matches(String normalizedQuery, boolean includeVillagerNames) {
        if (normalizedQuery == null || normalizedQuery.isBlank()) {
            return true;
        }
        boolean baseMatches = profession.toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || entityType.toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || readableLevelName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || dimension.toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || coordinatesText().toLowerCase(Locale.ROOT).contains(normalizedQuery);
        if (baseMatches) {
            return true;
        }
        return includeVillagerNames
                && (detectedName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || manualName().toLowerCase(Locale.ROOT).contains(normalizedQuery));
    }

    public String coordinatesText() {
        if (unknownPosition) {
            return "Unknown position";
        }
        return "x=" + Math.round(x) + ", y=" + Math.round(y) + ", z=" + Math.round(z);
    }

    public String levelProgressText() {
        if (levelNumber >= 5 || "Master".equalsIgnoreCase(level)) {
            return "Level progress: Max level";
        }
        if (nextLevelXp > 0) {
            int remaining = Math.max(0, nextLevelXp - villagerXp);
            return "Level progress: " + remaining + " XP to level up (" + villagerXp + "/" + nextLevelXp + ", " + levelProgressPercent() + "%)";
        }
        return "Level progress: Unknown";
    }

    public int levelProgressPercent() {
        if (nextLevelXp <= 0) {
            return -1;
        }
        return Math.max(0, Math.min(100, Math.round((villagerXp * 100.0F) / nextLevelXp)));
    }

    public String professionLevelText() {
        String readableLevel = readableLevelName();
        if (readableLevel.isBlank()) {
            return profession;
        }
        return profession + " - " + readableLevel;
    }

    public String namedProfessionText() {
        String name = villagerName();
        if (name.isBlank()) {
            return profession;
        }
        return name + " — " + profession;
    }

    public String readableLevelName() {
        if (level != null && !level.isBlank()) {
            return level;
        }
        return levelName(levelNumber);
    }

    private static String levelName(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> "Novice";
            case 2 -> "Apprentice";
            case 3 -> "Journeyman";
            case 4 -> "Expert";
            case 5 -> "Master";
            default -> "";
        };
    }

    private static String blankDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String cleanName(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.trim();
        return cleaned.length() > 80 ? cleaned.substring(0, 80) : cleaned;
    }
}
