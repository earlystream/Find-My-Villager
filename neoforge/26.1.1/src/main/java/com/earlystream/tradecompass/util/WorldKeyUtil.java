package com.earlystream.tradecompass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Locale;

public final class WorldKeyUtil {
    private WorldKeyUtil() {
    }

    public static String currentWorldKey(Minecraft client) {
        if (client == null || client.level == null) {
            return "";
        }
        String dimension = currentDimension(client);
        ServerData serverData = client.getCurrentServer();
        if (serverData != null) {
            return SafeFileNameUtil.safeKey("server", "server:" + serverData.ip + ":" + dimension);
        }
        String singleplayerName = "singleplayer";
        if (client.getSingleplayerServer() != null) {
            singleplayerName = client.getSingleplayerServer().getWorldData().getLevelName();
        }
        return SafeFileNameUtil.safeKey("singleplayer", "singleplayer:" + singleplayerName + ":" + dimension);
    }

    public static String currentDimension(Minecraft client) {
        if (client == null || client.level == null) {
            return "";
        }
        return dimensionKey(client.level);
    }

    public static String dimensionKey(Level level) {
        if (level == null) {
            return "";
        }
        ResourceKey<Level> key = level.dimension();
        return key.identifier().toString().toLowerCase(Locale.ROOT);
    }
}
