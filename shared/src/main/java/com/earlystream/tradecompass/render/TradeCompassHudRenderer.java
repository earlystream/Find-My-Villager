package com.earlystream.tradecompass.render;

import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

public final class TradeCompassHudRenderer {
    private static int cooldownTicks;

    private TradeCompassHudRenderer() {
    }

    public static void tick(Minecraft client, SearchResult selectedResult) {
        if (selectedResult == null || client.player == null || client.level == null) {
            cooldownTicks = 0;
            return;
        }
        if (cooldownTicks++ % 20 != 0) {
            return;
        }
        MerchantRecord merchant = selectedResult.merchant();
        String text;
        if (merchant.unknownPosition()) {
            text = "Trade Compass: unknown merchant position";
        } else if (!WorldKeyUtil.currentDimension(client).equals(merchant.dimension())) {
            text = "Trade Compass: " + selectedResult.offer().output().name() + " is in a different dimension";
        } else {
            AbstractVillager villager = TradeCompassWorldRenderer.targetVillager(client, selectedResult);
            double targetX = villager == null ? merchant.x() : villager.getX();
            double targetZ = villager == null ? merchant.z() : villager.getZ();
            double dx = targetX - client.player.getX();
            double dz = targetZ - client.player.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            text = "Trade Compass: " + selectedResult.offer().output().name() + " - " + Math.round(distance) + "m, " + direction(dx, dz);
        }
        client.gui.setOverlayMessage(Component.literal(text), false);
    }

    private static String direction(double dx, double dz) {
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? "east" : "west";
        }
        return dz > 0 ? "south" : "north";
    }
}
