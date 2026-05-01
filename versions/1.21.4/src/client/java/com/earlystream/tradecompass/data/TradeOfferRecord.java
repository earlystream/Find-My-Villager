package com.earlystream.tradecompass.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class TradeOfferRecord {
    private ItemStackRecord inputOne = ItemStackRecord.empty();
    private ItemStackRecord inputTwo = ItemStackRecord.empty();
    private ItemStackRecord output = ItemStackRecord.empty();
    private List<String> outputEnchantments = new ArrayList<>();
    private int currentPrice;
    private int basePrice;
    private boolean disabled;
    private int uses;
    private int maxUses;
    private int specialPrice;

    public ItemStackRecord inputOne() {
        if (inputOne == null) {
            inputOne = ItemStackRecord.empty();
        }
        return inputOne;
    }

    public void inputOne(ItemStackRecord inputOne) {
        this.inputOne = inputOne == null ? ItemStackRecord.empty() : inputOne;
    }

    public ItemStackRecord inputTwo() {
        if (inputTwo == null) {
            inputTwo = ItemStackRecord.empty();
        }
        return inputTwo;
    }

    public void inputTwo(ItemStackRecord inputTwo) {
        this.inputTwo = inputTwo == null ? ItemStackRecord.empty() : inputTwo;
    }

    public ItemStackRecord output() {
        if (output == null) {
            output = ItemStackRecord.empty();
        }
        return output;
    }

    public void output(ItemStackRecord output) {
        this.output = output == null ? ItemStackRecord.empty() : output;
    }

    public List<String> outputEnchantments() {
        if (outputEnchantments == null) {
            outputEnchantments = new ArrayList<>();
        }
        return outputEnchantments;
    }

    public void outputEnchantments(List<String> outputEnchantments) {
        this.outputEnchantments = outputEnchantments == null ? new ArrayList<>() : new ArrayList<>(outputEnchantments);
    }

    public int currentPrice() {
        return currentPrice;
    }

    public void currentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int basePrice() {
        return basePrice;
    }

    public void basePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public boolean disabled() {
        return disabled;
    }

    public void disabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int uses() {
        return uses;
    }

    public void uses(int uses) {
        this.uses = uses;
    }

    public int maxUses() {
        return maxUses;
    }

    public void maxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int specialPrice() {
        return specialPrice;
    }

    public void specialPrice(int specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String priceText() {
        StringJoiner joiner = new StringJoiner(" + ");
        if (!inputOne().isEmpty()) {
            joiner.add(inputOne().priceText());
        }
        if (!inputTwo().isEmpty()) {
            joiner.add(inputTwo().priceText());
        }
        String value = joiner.toString();
        return value.isBlank() ? "Unknown price" : value;
    }

    public String displayOutputName() {
        if (outputEnchantments().isEmpty()) {
            return output().name();
        }
        return output().name() + " (" + String.join(", ", outputEnchantments()) + ")";
    }

    public String stockText() {
        return "Stock: " + remainingUses() + " / " + Math.max(0, maxUses) + " trades remaining";
    }

    public int remainingUses() {
        return Math.max(0, maxUses - uses);
    }

    public boolean inStock() {
        return remainingUses() > 0;
    }

    public boolean soldOut() {
        return remainingUses() <= 0;
    }

    public int availableOutputItems() {
        return remainingUses() * Math.max(0, output().count());
    }

    public String availableOutputText() {
        return "Available output: " + availableOutputItems() + " items before restock";
    }

    public String restockText() {
        if (inStock()) {
            return "Restock: In stock";
        }
        return "Restock: Unknown, villager must restock at workstation";
    }

    public String compactText() {
        return displayOutputName() + " - " + priceText() + " - " + stockText();
    }

    public boolean matches(String normalizedQuery) {
        if (normalizedQuery == null || normalizedQuery.isBlank()) {
            return true;
        }
        if ("cheap".equals(normalizedQuery) || "cheapest".equals(normalizedQuery)) {
            return true;
        }
        if (output().matches(normalizedQuery) || inputOne().matches(normalizedQuery) || inputTwo().matches(normalizedQuery)) {
            return true;
        }
        if (Integer.toString(currentPrice).equals(normalizedQuery) || priceText().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
            return true;
        }
        for (String enchantment : outputEnchantments()) {
            if (enchantment != null && enchantment.toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                return true;
            }
        }
        return false;
    }
}
