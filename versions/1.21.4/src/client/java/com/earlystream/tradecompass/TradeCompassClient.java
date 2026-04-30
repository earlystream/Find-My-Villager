package com.earlystream.tradecompass;

import com.earlystream.tradecompass.capture.TradeCaptureService;
import com.earlystream.tradecompass.config.TradeCompassConfig;
import com.earlystream.tradecompass.config.TradeCompassSettings;
import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.data.TradeCompassStorage;
import com.earlystream.tradecompass.data.WorldTradeDatabase;
import com.earlystream.tradecompass.gui.TradeCompassScreen;
import com.earlystream.tradecompass.keybind.TradeCompassKeybinds;
import com.earlystream.tradecompass.render.TradeCompassHudRenderer;
import com.earlystream.tradecompass.render.TradeCompassTargetHudRenderer;
import com.earlystream.tradecompass.render.TradeCompassWorldRenderer;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class TradeCompassClient implements ClientModInitializer {
    private static TradeCompassStorage storage;
    private static TradeCompassSettings settings = new TradeCompassSettings();
    private static WorldTradeDatabase database = new WorldTradeDatabase();
    private static String loadedWorldKey = "";
    private static SearchResult selectedResult;
    private static boolean rawOpenKeyWasDown;
    private static boolean shownLoadedMessage;

    @Override
    public void onInitializeClient() {
        storage = new TradeCompassStorage(FabricLoader.getInstance().getConfigDir());
        settings = storage.loadSettings();
        storage.saveSettings(settings);
        TradeCompassKeybinds.register();
        TradeCompassTargetHudRenderer.register();
        ClientTickEvents.END_CLIENT_TICK.register(TradeCompassClient::tick);
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            TradeCompassWorldRenderer.tick(client, null);
            return;
        }
        ensureWorldLoaded(client);
        if (!shownLoadedMessage && client.gui != null) {
            client.gui.setOverlayMessage(Component.literal("Find My Villager loaded - press V to open"), false);
            shownLoadedMessage = true;
        }
        while (TradeCompassKeybinds.openKey().consumeClick()) {
            client.setScreen(new TradeCompassScreen());
        }
        boolean rawOpenKeyDown = InputConstants.isKeyDown(client.getWindow().getWindow(), GLFW.GLFW_KEY_V);
        if (rawOpenKeyDown && !rawOpenKeyWasDown && client.screen == null) {
            client.setScreen(new TradeCompassScreen());
        }
        rawOpenKeyWasDown = rawOpenKeyDown;
        TradeCaptureService.tick(client);
        TradeCompassHudRenderer.tick(client, selectedResult);
        TradeCompassWorldRenderer.tick(client, selectedResult);
    }

    public static void ensureWorldLoaded(Minecraft client) {
        String worldKey = WorldKeyUtil.currentWorldKey(client);
        if (worldKey.isBlank() || worldKey.equals(loadedWorldKey)) {
            return;
        }
        loadedWorldKey = worldKey;
        database = storage.loadWithFallback(worldKey);
        database.worldKey(worldKey);
        selectedResult = null;
    }

    public static void reloadCurrentWorld(Minecraft client) {
        if (storage == null) {
            return;
        }
        String worldKey = WorldKeyUtil.currentWorldKey(client);
        if (worldKey.isBlank()) {
            return;
        }
        loadedWorldKey = worldKey;
        database = storage.loadWithFallback(worldKey);
        database.worldKey(worldKey);
        selectedResult = null;
    }

    public static WorldTradeDatabase database() {
        return database;
    }

    public static String loadedWorldKey() {
        return loadedWorldKey;
    }

    public static void save() {
        if (storage != null) {
            storage.save(database);
        }
    }

    public static TradeCompassSettings settings() {
        return settings;
    }

    public static void saveSettings() {
        if (storage != null) {
            storage.saveSettings(settings);
        }
    }

    public static void upsert(MerchantRecord record) {
        database.upsert(record);
        save();
    }

    public static void select(SearchResult result) {
        selectedResult = result;
    }

    public static SearchResult selectedResult() {
        return selectedResult;
    }

    public static void clearSelected() {
        selectedResult = null;
    }

    public static String modName() {
        return TradeCompassConfig.MOD_NAME;
    }
}
