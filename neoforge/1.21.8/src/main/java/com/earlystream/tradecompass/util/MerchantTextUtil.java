package com.earlystream.tradecompass.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.Locale;

public final class MerchantTextUtil {
    private static final String[] LEVELS = {"", "Novice", "Apprentice", "Journeyman", "Expert", "Master"};

    private MerchantTextUtil() {
    }

    public static String entityTypeName(Entity entity) {
        if (entity == null) {
            return "Unknown merchant";
        }
        return title(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath());
    }

    public static String entityTypeId(Entity entity) {
        if (entity == null) {
            return "";
        }
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    public static String professionId(Entity entity) {
        if (entity instanceof Villager villager) {
            VillagerProfession profession = villager.getVillagerData().profession().value();
            return BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        }
        return "";
    }

    public static String professionName(Entity entity) {
        if (entity instanceof Villager villager) {
            VillagerProfession profession = villager.getVillagerData().profession().value();
            return title(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getPath());
        }
        return entityTypeName(entity);
    }

    public static String villagerCustomName(Entity entity) {
        if (entity == null) {
            return "";
        }
        return componentText(entity.getCustomName());
    }

    public static String villagerLevel(Entity entity) {
        if (entity instanceof Villager villager) {
            VillagerData data = villager.getVillagerData();
            int level = data.level();
            if (level >= 1 && level < LEVELS.length) {
                return LEVELS[level];
            }
        }
        return "";
    }

    public static int villagerLevelNumber(Entity entity) {
        if (entity instanceof Villager villager) {
            return villager.getVillagerData().level();
        }
        return 0;
    }

    public static int villagerXp(Entity entity) {
        if (entity instanceof Villager villager) {
            return villager.getVillagerXp();
        }
        return 0;
    }

    public static int nextVillagerLevelXp(Entity entity) {
        if (entity instanceof Villager villager) {
            int level = villager.getVillagerData().level();
            if (level >= 5) {
                return -1;
            }
            return VillagerData.getMaxXpPerLevel(level);
        }
        return -1;
    }

    private static String componentText(Component component) {
        return component == null ? "" : component.getString().trim();
    }

    private static String title(String value) {
        if (value == null || value.isBlank()) {
            return "Unknown";
        }
        String[] parts = value.replace('_', ' ').split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
