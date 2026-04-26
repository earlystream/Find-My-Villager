package com.earlystream.tradecompass.capture;

import com.earlystream.tradecompass.TradeCompassClient;
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

    private TradeCaptureService() {
    }

    public static void tick(Minecraft client) {
        if (!(client.screen instanceof MerchantScreen merchantScreen)) {
            lastCapturedMenu = -1;
            lastCapturedOfferCount = -1;
            lastCapturedOfferSignature = 0;
            return;
        }
        MerchantMenu menu = merchantScreen.getMenu();
        MerchantOffers offers = menu.getOffers();
        captureMenu(client, menu, offers);
    }

    public static void captureMenu(Minecraft client, MerchantMenu menu, MerchantOffers offers) {
        if (client == null || menu == null) {
            return;
        }
        int menuIdentity = System.identityHashCode(menu);
        int offerCount = offers == null ? 0 : offers.size();
        int offerSignature = offerSignature(offers);
        if (offerCount <= 0 || (menuIdentity == lastCapturedMenu && offerCount == lastCapturedOfferCount && offerSignature == lastCapturedOfferSignature)) {
            return;
        }
        lastCapturedMenu = menuIdentity;
        lastCapturedOfferCount = offerCount;
        lastCapturedOfferSignature = offerSignature;
        capture(client, menu, offers);
    }

    private static void capture(Minecraft client, MerchantMenu menu, MerchantOffers offers) {
        PendingMerchantInteraction pending = MerchantInteractionTracker.takeFresh();
        String worldKey = TradeCompassClient.loadedWorldKey();
        if (worldKey.isBlank()) {
            TradeCompassClient.ensureWorldLoaded(client);
            worldKey = TradeCompassClient.loadedWorldKey();
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
        TradeCompassClient.upsert(record);
        if (client.gui != null) {
            client.gui.setOverlayMessage(Component.literal(
                    "Trade Compass saved " + capturedOffers.size() + " trades for " + record.professionLevelText()
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
