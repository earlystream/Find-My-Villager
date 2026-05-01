package com.earlystream.tradecompass.render;

import com.earlystream.tradecompass.TradeCompassMod;
import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

public final class TradeCompassTargetHudRenderer {
    private static final float ARROW_SCALE = 1.875F;

    private TradeCompassTargetHudRenderer() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        render(graphics, deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    private static void render(GuiGraphics graphics, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }
        SearchResult selected = TradeCompassMod.selectedResult();
        if (selected == null) {
            return;
        }
        MerchantRecord merchant = selected.merchant();
        if (merchant == null || merchant.unknownPosition() || !WorldKeyUtil.currentDimension(client).equals(merchant.dimension())) {
            return;
        }
        AbstractVillager villager = TradeCompassWorldRenderer.targetVillager(client, selected);
        double targetX = villager == null ? merchant.x() : villager.getX();
        double targetZ = villager == null ? merchant.z() : villager.getZ();
        double dx = targetX - client.player.getX();
        double dz = targetZ - client.player.getZ();
        String arrow = directionArrow(client.player.getYRot(), dx, dz);
        int x = Math.round((client.getWindow().getGuiScaledWidth() - client.font.width(arrow) * ARROW_SCALE) / 2.0F);
        int y = client.getWindow().getGuiScaledHeight() - 90;
        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(x, y);
            graphics.pose().scale(ARROW_SCALE);
            graphics.drawString(client.font, arrow, 0, 0, 0xFFFFE066, true);
        } finally {
            graphics.pose().popMatrix();
        }
    }

    private static String directionArrow(float playerYaw, double dx, double dz) {
        double yaw = Math.toRadians(playerYaw);
        double forward = dx * -Math.sin(yaw) + dz * Math.cos(yaw);
        double right = dx * -Math.cos(yaw) + dz * -Math.sin(yaw);
        if (Math.abs(right) > Math.abs(forward)) {
            return right > 0.0D ? "➡" : "⬅";
        }
        return forward > 0.0D ? "⬆" : "⬇";
    }
}
