package com.earlystream.tradecompass.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorldTradeDatabase {
    private String worldKey = "";
    private Map<String, MerchantRecord> merchants = new LinkedHashMap<>();

    public String worldKey() {
        return worldKey;
    }

    public void worldKey(String worldKey) {
        this.worldKey = worldKey == null ? "" : worldKey;
    }

    public Collection<MerchantRecord> merchants() {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        return merchants.values();
    }

    public MerchantRecord merchant(String key) {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        return merchants.get(key);
    }

    public void upsert(MerchantRecord record) {
        if (record == null || record.merchantKey().isBlank()) {
            return;
        }
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        record.copyUserFieldsFrom(merchants.get(record.merchantKey()));
        merchants.put(record.merchantKey(), record);
    }

    public void put(MerchantRecord record) {
        if (record == null || record.merchantKey().isBlank()) {
            return;
        }
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        merchants.put(record.merchantKey(), record);
    }

    public void delete(String merchantKey) {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        merchants.remove(merchantKey);
    }

    public void clear() {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        merchants.clear();
    }

    public int size() {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        return merchants.size();
    }

    public ArrayList<MerchantRecord> merchantList() {
        if (merchants == null) {
            merchants = new LinkedHashMap<>();
        }
        return new ArrayList<>(merchants.values());
    }
}
