package com.earlystream.tradecompass.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class TradeCompassKeybinds {
    private static KeyMapping openKey;
    private static final String CATEGORY = "category.tradecompass";

    private TradeCompassKeybinds() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        openKey = new KeyMapping(
                "key.tradecompass.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        );
        event.register(openKey);
    }

    public static KeyMapping openKey() {
        return openKey;
    }
}
