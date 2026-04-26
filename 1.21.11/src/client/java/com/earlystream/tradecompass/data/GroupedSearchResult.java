package com.earlystream.tradecompass.data;

import java.util.List;

public record GroupedSearchResult(MerchantRecord merchant, List<TradeOfferRecord> matchingOffers, double distance) {
    public TradeOfferRecord primaryOffer() {
        return matchingOffers.isEmpty() ? null : matchingOffers.get(0);
    }
}
