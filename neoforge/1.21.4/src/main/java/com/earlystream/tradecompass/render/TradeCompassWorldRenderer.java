package com.earlystream.tradecompass.render;

import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.mixin.EntityGlowAccessor;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;

import java.util.UUID;

public final class TradeCompassWorldRenderer {
    private static UUID highlightedUuid;
    private static Entity highlightedEntity;
    private static boolean highlightedEntityWasGlowing;

    private TradeCompassWorldRenderer() {
    }

    public static void tick(Minecraft client, SearchResult selectedResult) {
        AbstractVillager target = targetVillager(client, selectedResult);
        if (target == highlightedEntity) {
            if (target != null) {
                applyHighlight(target);
            }
            return;
        }
        clearHighlight();
        if (target == null) {
            return;
        }
        highlightedUuid = target.getUUID();
        highlightedEntity = target;
        highlightedEntityWasGlowing = target.isCurrentlyGlowing();
        applyHighlight(target);
    }

    public static AbstractVillager targetVillager(Minecraft client, SearchResult selectedResult) {
        if (selectedResult == null || client == null || client.level == null) {
            return null;
        }
        MerchantRecord merchant = selectedResult.merchant();
        if (merchant == null || merchant.entityUuid().isBlank()) {
            return null;
        }
        if (!WorldKeyUtil.currentDimension(client).equals(merchant.dimension())) {
            return null;
        }
        ClientLevel level = client.level;
        UUID uuid;
        try {
            uuid = UUID.fromString(merchant.entityUuid());
        } catch (IllegalArgumentException exception) {
            return null;
        }
        for (Entity entity : level.entitiesForRendering()) {
            if (uuid.equals(entity.getUUID()) && entity instanceof AbstractVillager villager) {
                return villager;
            }
        }
        return null;
    }

    private static void clearHighlight() {
        if (highlightedEntity != null && highlightedEntity.isAlive()
                && highlightedUuid != null && highlightedUuid.equals(highlightedEntity.getUUID())) {
            ((EntityGlowAccessor) highlightedEntity).tradecompass$setSharedFlag(6, highlightedEntityWasGlowing);
        }
        highlightedUuid = null;
        highlightedEntity = null;
        highlightedEntityWasGlowing = false;
    }

    private static void applyHighlight(Entity entity) {
        ((EntityGlowAccessor) entity).tradecompass$setSharedFlag(6, true);
    }
}
