package com.earlystream.tradecompass.capture;

import com.earlystream.tradecompass.TradeCompassMod;
import com.earlystream.tradecompass.data.ItemStackRecord;
import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.TradeOfferRecord;
import com.earlystream.tradecompass.util.EnchantmentTextUtil;
import com.earlystream.tradecompass.util.ItemTextUtil;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.ArrayList;
import java.util.List;

public final class TradeCaptureService {
    private static int lastCapturedMenu = -1;
    private static int lastCapturedOfferCount = -1;
    private static int lastCapturedOfferSignature = 0;

    private static MerchantMenu pendingMenu = null;
    private static MerchantOffers pendingOffers = null;
    private static long pendingMarkedMs = 0;
    private static final long DEBOUNCE_MS = 250;

    private TradeCaptureService() {
    }

    public static void tick(Minecraft client) {
        if (!(client.screen instanceof MerchantScreen)) {
            lastCapturedMenu = -1;
            lastCapturedOfferCount = -1;
            lastCapturedOfferSignature = 0;
            pendingMenu = null;
            pendingOffers = null;
            return;
        }
        capturePending(client, true);
    }

    public static void captureMenu(Minecraft client, MerchantMenu menu, MerchantOffers offers) {
        if (client == null || menu == null) {
            return;
        }
        int offerCount = offers == null ? 0 : offers.size();
        if (offerCount <= 0) {
            return;
        }
        int menuIdentity = System.identityHashCode(menu);
        int offerSignature = offerSignature(offers);
        if (menuIdentity == lastCapturedMenu && offerCount == lastCapturedOfferCount && offerSignature == lastCapturedOfferSignature) {
            return;
        }
        pendingMenu = menu;
        pendingOffers = offers;
        pendingMarkedMs = System.currentTimeMillis();
        capturePending(client, false);
    }

    private static void capturePending(Minecraft client, boolean requireDebounce) {
        if (pendingMenu == null || (requireDebounce && System.currentTimeMillis() - pendingMarkedMs < DEBOUNCE_MS)) {
            return;
        }
        MerchantMenu menu = pendingMenu;
        MerchantOffers offers = pendingOffers;
        pendingMenu = null;
        pendingOffers = null;
        int menuIdentity = System.identityHashCode(menu);
        int offerCount = offers == null ? 0 : offers.size();
        int offerSignature = offerSignature(offers);
        if (offerCount > 0 && !(menuIdentity == lastCapturedMenu && offerCount == lastCapturedOfferCount && offerSignature == lastCapturedOfferSignature)) {
            lastCapturedMenu = menuIdentity;
            lastCapturedOfferCount = offerCount;
            lastCapturedOfferSignature = offerSignature;
            capture(client, menu, offers);
        }
    }

    private static void capture(Minecraft client, MerchantMenu menu, MerchantOffers offers) {
        PendingMerchantInteraction pending = MerchantInteractionTracker.takeFresh();
        String worldKey = TradeCompassMod.loadedWorldKey();
        if (worldKey.isBlank()) {
            TradeCompassMod.ensureWorldLoaded(client);
            worldKey = TradeCompassMod.loadedWorldKey();
        }
        if (worldKey.isBlank()) {
            worldKey = WorldKeyUtil.currentWorldKey(client);
        }
        MerchantRecord record = new MerchantRecord();
        record.worldKey(worldKey);
        record.lastScannedEpochMillis(System.currentTimeMillis());
        if (pending == null) {
            record.merchantKey("unknown-" + record.lastScannedEpochMillis());
            record.entityType("Unknown merchant");
            record.profession("Unknown");
            record.dimension(WorldKeyUtil.currentDimension(client));
            record.unknownPosition(true);
        } else {
            record.merchantKey(pending.merchantKey(worldKey));
            record.entityUuid(pending.entityUuid());
            record.entityTypeId(pending.entityTypeId());
            record.entityType(pending.entityType());
            record.professionId(pending.professionId());
            record.profession(pending.profession());
            record.level(pending.level());
            record.levelNumber(pending.levelNumber());
            record.villagerXp(pending.villagerXp());
            record.nextLevelXp(pending.nextLevelXp());
            record.dimension(pending.dimension());
            record.x(pending.x());
            record.y(pending.y());
            record.z(pending.z());
            record.unknownPosition(false);
        }
        applyMenuLevelData(record, menu);
        List<TradeOfferRecord> capturedOffers = new ArrayList<>();
        for (MerchantOffer offer : offers) {
            capturedOffers.add(captureOffer(offer));
        }
        record.offers(capturedOffers);
        TradeCompassMod.upsert(record);
        if (client.gui != null) {
            client.gui.setOverlayMessage(Component.literal(
                    "Find My Villager saved " + capturedOffers.size() + " trades for " + record.professionLevelText()
            ), false);
        }
    }

    private static int offerSignature(MerchantOffers offers) {
        if (offers == null || offers.isEmpty()) {
            return 0;
        }
        int signature = 1;
        for (MerchantOffer offer : offers) {
            signature = 31 * signature + ItemTextUtil.fromStack(offer.getCostA()).hashCode();
            signature = 31 * signature + ItemTextUtil.fromStack(offer.getCostB()).hashCode();
            signature = 31 * signature + ItemTextUtil.fromStack(offer.getResult()).hashCode();
            signature = 31 * signature + offer.getUses();
            signature = 31 * signature + offer.getMaxUses();
            signature = 31 * signature + offer.getSpecialPriceDiff();
            signature = 31 * signature + (offer.isOutOfStock() ? 1 : 0);
        }
        return signature;
    }

    private static void applyMenuLevelData(MerchantRecord record, MerchantMenu menu) {
        int level = menu.getTraderLevel();
        if (level >= 1 && level <= 5) {
            record.levelNumber(level);
            record.level(levelName(level));
            if (menu.showProgressBar() && level < 5) {
                record.villagerXp(menu.getTraderXp());
                record.nextLevelXp(VillagerData.getMaxXpPerLevel(level));
            } else {
                record.villagerXp(0);
                record.nextLevelXp(-1);
            }
        }
    }

    private static String levelName(int level) {
        return switch (level) {
            case 1 -> "Novice";
            case 2 -> "Apprentice";
            case 3 -> "Journeyman";
            case 4 -> "Expert";
            case 5 -> "Master";
            default -> "";
        };
    }

    private static TradeOfferRecord captureOffer(MerchantOffer offer) {
        TradeOfferRecord record = new TradeOfferRecord();
        ItemStackRecord inputOne = ItemTextUtil.fromStack(offer.getCostA());
        record.inputOne(inputOne);
        record.inputTwo(ItemTextUtil.fromStack(offer.getCostB()));
        record.output(ItemTextUtil.fromStack(offer.getResult()));
        record.outputEnchantments(EnchantmentTextUtil.fromStack(offer.getResult()));
        record.currentPrice(inputOne.count());
        record.basePrice(ItemTextUtil.fromStack(offer.getBaseCostA()).count());
        record.disabled(offer.isOutOfStock());
        record.uses(offer.getUses());
        record.maxUses(offer.getMaxUses());
        record.specialPrice(offer.getSpecialPriceDiff());
        return record;
    }
}
