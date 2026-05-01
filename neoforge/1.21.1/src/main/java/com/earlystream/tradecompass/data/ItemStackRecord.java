package com.earlystream.tradecompass.data;

import java.util.Locale;

public class ItemStackRecord {
    private String itemId = "minecraft:air";
    private String name = "Air";
    private int count = 0;

    public ItemStackRecord() {
    }

    public ItemStackRecord(String itemId, String name, int count) {
        this.itemId = itemId == null ? "minecraft:air" : itemId;
        this.name = name == null ? "Air" : name;
        this.count = count;
    }

    public static ItemStackRecord empty() {
        return new ItemStackRecord("minecraft:air", "Air", 0);
    }

    public String itemId() {
        return itemId;
    }

    public String name() {
        return name;
    }

    public int count() {
        return count;
    }

    public boolean isEmpty() {
        return count <= 0 || itemId == null || itemId.isBlank() || "minecraft:air".equals(itemId);
    }

    public String priceText() {
        if (isEmpty()) {
            return "";
        }
        return count + " " + name;
    }

    public boolean matches(String normalizedQuery) {
        if (normalizedQuery == null || normalizedQuery.isBlank()) {
            return true;
        }
        return normalize(name).contains(normalizedQuery) || normalize(itemId).contains(normalizedQuery);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
