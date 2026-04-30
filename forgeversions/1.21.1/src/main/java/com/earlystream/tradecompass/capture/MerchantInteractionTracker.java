package com.earlystream.tradecompass.capture;

import com.earlystream.tradecompass.config.TradeCompassConfig;
import com.earlystream.tradecompass.util.MerchantTextUtil;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;

public final class MerchantInteractionTracker {
    private static PendingMerchantInteraction pending;

    private MerchantInteractionTracker() {
    }

    public static void remember(Entity entity) {
        if (!(entity instanceof AbstractVillager)) {
            return;
        }
        pending = new PendingMerchantInteraction(
                entity.getUUID().toString(),
                MerchantTextUtil.entityTypeId(entity),
                MerchantTextUtil.entityTypeName(entity),
                MerchantTextUtil.professionId(entity),
                MerchantTextUtil.professionName(entity),
                MerchantTextUtil.villagerCustomName(entity),
                MerchantTextUtil.villagerLevel(entity),
                MerchantTextUtil.villagerLevelNumber(entity),
                MerchantTextUtil.villagerXp(entity),
                MerchantTextUtil.nextVillagerLevelXp(entity),
                WorldKeyUtil.dimensionKey(entity.level()),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                System.currentTimeMillis()
        );
    }

    public static PendingMerchantInteraction takeFresh() {
        long now = System.currentTimeMillis();
        if (pending == null || pending.expired(now, TradeCompassConfig.PENDING_INTERACTION_TTL_MILLIS)) {
            pending = null;
            return null;
        }
        PendingMerchantInteraction value = pending;
        pending = null;
        return value;
    }
}
