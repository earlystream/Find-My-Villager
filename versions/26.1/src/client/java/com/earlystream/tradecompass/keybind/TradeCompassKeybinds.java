package com.earlystream.tradecompass.keybind;

import com.earlystream.tradecompass.config.TradeCompassConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class TradeCompassKeybinds {
    private static KeyMapping openKey;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(TradeCompassConfig.MOD_ID, "category")
    );

    private TradeCompassKeybinds() {
    }

    public static void register() {
        openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.tradecompass.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));
    }

    public static KeyMapping openKey() {
        return openKey;
    }
}
