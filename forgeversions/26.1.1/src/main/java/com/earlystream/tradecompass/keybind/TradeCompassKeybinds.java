package com.earlystream.tradecompass.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class TradeCompassKeybinds {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("tradecompass", "tradecompass")
    );
    private static KeyMapping openKey;

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
