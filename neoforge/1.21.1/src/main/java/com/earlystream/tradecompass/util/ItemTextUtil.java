package com.earlystream.tradecompass.util;

import com.earlystream.tradecompass.data.ItemStackRecord;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public final class ItemTextUtil {
    private ItemTextUtil() {
    }

    public static ItemStackRecord fromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStackRecord.empty();
        }
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String name = stack.getHoverName().getString();
        return new ItemStackRecord(id, name, stack.getCount());
    }
}
