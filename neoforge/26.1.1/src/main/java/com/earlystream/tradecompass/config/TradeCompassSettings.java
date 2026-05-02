package com.earlystream.tradecompass.config;

public class TradeCompassSettings {
    private Boolean showVillagerNamesInSearch = true;

    public boolean showVillagerNamesInSearch() {
        return showVillagerNamesInSearch == null || showVillagerNamesInSearch;
    }

    public void showVillagerNamesInSearch(boolean showVillagerNamesInSearch) {
        this.showVillagerNamesInSearch = showVillagerNamesInSearch;
    }
}
