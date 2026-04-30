package com.earlystream.tradecompass.keybind;

import com.earlystream.tradecompass.config.TradeCompassConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class TradeCompassKeybinds {
    private static KeyMapping openKey;
    private static final String CATEGORY = "category.tradecompass";

    private TradeCompassKeybinds() {
    }

    public static void register() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
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
