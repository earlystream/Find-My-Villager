package com.earlystream.tradecompass.render;

import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public final class TradeCompassWorldRenderer {
    private static UUID highlightedUuid;
    private static Entity highlightedEntity;
    private static boolean highlightedEntityWasGlowing;
    private static boolean highlightedEntityHadGlowingEffect;

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
        highlightedEntityHadGlowingEffect = target instanceof LivingEntity living && living.hasEffect(MobEffects.GLOWING);
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
        Entity loadedEntity = level.getEntity(uuid);
        if (loadedEntity instanceof AbstractVillager villager) {
            return villager;
        }
        for (Entity entity : level.entitiesForRendering()) {
            if (uuid.equals(entity.getUUID()) && entity instanceof AbstractVillager villager) {
                return villager;
            }
        }
        return null;
    }

    private static void clearHighlight() {
        if (highlightedEntity != null && highlightedEntity.isAlive() && highlightedUuid != null && highlightedUuid.equals(highlightedEntity.getUUID())) {
            if (highlightedEntity instanceof LivingEntity living && !highlightedEntityHadGlowingEffect) {
                living.removeEffect(MobEffects.GLOWING);
            }
            highlightedEntity.setGlowingTag(highlightedEntityWasGlowing);
        }
        highlightedUuid = null;
        highlightedEntity = null;
        highlightedEntityWasGlowing = false;
        highlightedEntityHadGlowingEffect = false;
    }

    private static void applyHighlight(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false, false));
        }
        entity.setGlowingTag(true);
    }
}
