package com.earlystream.tradecompass.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public final class EnchantmentTextUtil {
    private EnchantmentTextUtil() {
    }

    public static List<String> fromStack(ItemStack stack) {
        List<String> enchantments = new ArrayList<>();
        if (stack == null || stack.isEmpty()) {
            return enchantments;
        }
        addEnchantments(enchantments, stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        addEnchantments(enchantments, stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
        return enchantments;
    }

    private static void addEnchantments(List<String> output, ItemEnchantments enchantments) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            String name = entry.getKey().value().description().getString();
            int level = entry.getIntValue();
            output.add(name + " " + roman(level));
        }
    }

    private static String roman(int value) {
        return switch (value) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> Integer.toString(value);
        };
    }
}
