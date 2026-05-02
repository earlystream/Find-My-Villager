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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = TradeCompassConfig.MOD_ID, dist = Dist.CLIENT)
public final class TradeCompassMod {
    private static TradeCompassStorage storage;
    private static TradeCompassSettings settings = new TradeCompassSettings();
    private static WorldTradeDatabase database = new WorldTradeDatabase();
    private static String loadedWorldKey = "";
    private static SearchResult selectedResult;
    private static boolean rawOpenKeyWasDown;
    private static boolean shownLoadedMessage;

    public TradeCompassMod(IEventBus modBus) {
        modBus.addListener(TradeCompassKeybinds::onRegisterKeyMappings);
        modBus.addListener(TradeCompassMod::onRegisterGuiLayers);

        storage = new TradeCompassStorage(FMLPaths.CONFIGDIR.get());
        settings = storage.loadSettings();
        storage.saveSettings(settings);
        NeoForge.EVENT_BUS.addListener(TradeCompassMod::onClientTick);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            ResourceLocation.fromNamespaceAndPath("tradecompass", "direction_hud"),
            TradeCompassTargetHudRenderer::render
        );
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
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

    public static TradeCompassStorage storage() {
        return storage;
    }

    public static String loadedWorldKey() {
        return loadedWorldKey;
    }

    public static void save() {
        if (storage != null) {
            storage.save(database);
        }
    }

    public static void replaceDatabase(WorldTradeDatabase newDatabase) {
        if (newDatabase == null) {
            return;
        }
        if (loadedWorldKey != null && !loadedWorldKey.isBlank()) {
            newDatabase.worldKey(loadedWorldKey);
        }
        database = newDatabase;
        selectedResult = null;
        save();
    }

    public static void resetCurrentDatabase() {
        WorldTradeDatabase empty = new WorldTradeDatabase();
        empty.worldKey(loadedWorldKey);
        replaceDatabase(empty);
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
        if (record != null && database.worldKey().isBlank() && !record.worldKey().isBlank()) {
            database.worldKey(record.worldKey());
            loadedWorldKey = record.worldKey();
        }
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


    public static String modVersion() {
        return "0.3.0+1.21.4-neoforge";
    }
}
