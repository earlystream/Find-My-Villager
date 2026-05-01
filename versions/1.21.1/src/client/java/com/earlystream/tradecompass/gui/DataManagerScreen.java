package com.earlystream.tradecompass.gui;

import com.earlystream.tradecompass.TradeCompassClient;
import com.earlystream.tradecompass.data.DatabaseBackupService;
import com.earlystream.tradecompass.data.DatabaseExportService;
import com.earlystream.tradecompass.data.DatabaseImportService;
import com.earlystream.tradecompass.data.DatabaseStats;
import com.earlystream.tradecompass.data.ImportConflictStrategy;
import com.earlystream.tradecompass.data.ImportResult;
import com.earlystream.tradecompass.data.TradeCompassStorage;
import com.earlystream.tradecompass.util.TimeTextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataManagerScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("FindMyVillager/Data");
    private final Screen parent;
    private DatabaseStats stats;
    private String status = "";
    private long resetPendingUntil = 0L;
    private Button resetButton;

    public DataManagerScreen(Screen parent) {
        super(Component.literal("Data Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        refreshStats();
        int center = width / 2;
        int top = Math.max(70, height / 2 - 78);
        addRenderableWidget(Button.builder(Component.literal("Export JSON"), button -> exportJson()).bounds(center - 154, top, 148, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Import JSON"), button -> importJson()).bounds(center + 6, top, 148, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Backup Now"), button -> backupNow()).bounds(center - 154, top + 28, 148, 20).build());
        resetButton = addRenderableWidget(Button.builder(Component.literal("Reset Current Database"), button -> resetCurrentDatabase()).bounds(center + 6, top + 28, 148, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Back"), button -> Minecraft.getInstance().setScreen(parent)).bounds(center - 50, top + 66, 100, 20).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (resetPendingUntil > 0 && System.currentTimeMillis() > resetPendingUntil) {
            resetPendingUntil = 0L;
            if (resetButton != null) {
                resetButton.setMessage(Component.literal("Reset Current Database"));
            }
        }
        graphics.fill(0, 0, width, height, 0xD0101010);
        graphics.fill(0, 0, width, 48, 0x18FFFFFF);
        graphics.drawString(font, "Find My Villager - Data Manager", 18, 16, 0xFFFFFFFF, false);
        renderStats(graphics);
        renderStatus(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderStats(GuiGraphics graphics) {
        int left = Math.max(24, width / 2 - 190);
        int top = 62;
        int right = Math.min(width - 24, width / 2 + 190);
        graphics.fill(left - 10, top - 10, right + 10, top + 112, 0x40202020);
        drawStat(graphics, "Current database", stats.databaseId().isBlank() ? "No active world/server" : stats.databaseId(), left, top);
        drawStat(graphics, "Saved villagers", Integer.toString(stats.villagerCount()), left, top + 16);
        drawStat(graphics, "Saved trades", Integer.toString(stats.tradeCount()), left, top + 32);
        drawStat(graphics, "Favorites", Integer.toString(stats.favoriteCount()), left, top + 48);
        drawStat(graphics, "Known databases", Integer.toString(stats.knownDatabaseCount()), left, top + 64);
        drawStat(graphics, "Last backup", lastBackupText(), left, top + 80);
    }

    private void drawStat(GuiGraphics graphics, String label, String value, int x, int y) {
        graphics.drawString(font, label + ":", x, y, 0xFF9AA8B8, false);
        graphics.drawString(font, value, x + 112, y, 0xFFFFFFFF, false);
    }

    private void renderStatus(GuiGraphics graphics) {
        if (status.isBlank()) {
            TradeCompassStorage storage = TradeCompassClient.storage();
            if (storage != null) {
                graphics.drawString(font, "Exports, imports, and backups are stored under " + storage.tradeCompassDirectory(), 24, height - 18, 0xFF707070, false);
            }
            return;
        }
        graphics.drawString(font, status, 24, height - 18, 0xFFB8D7FF, false);
    }

    private String lastBackupText() {
        if (stats.lastBackupEpochMillis() <= 0L) {
            return "Never";
        }
        String fileName = stats.lastBackupPath() == null ? "" : " (" + stats.lastBackupPath().getFileName() + ")";
        return TimeTextUtil.ago(stats.lastBackupEpochMillis(), System.currentTimeMillis()) + fileName;
    }

    private void exportJson() {
        TradeCompassStorage storage = TradeCompassClient.storage();
        if (storage == null) {
            status = "Storage is not ready.";
            return;
        }
        try {
            Path exportPath = DatabaseExportService.exportCurrent(storage, TradeCompassClient.database(), TradeCompassClient.modVersion());
            status = "Exported " + exportPath.getFileName();
            refreshStats();
        } catch (IOException exception) {
            LOGGER.warn("Failed to export Find My Villager database", exception);
            status = "Export failed. See the game log for details.";
        }
    }

    private void importJson() {
        TradeCompassStorage storage = TradeCompassClient.storage();
        if (storage == null) {
            status = "Storage is not ready.";
            return;
        }
        Path source = chooseImportPath(storage);
        if (source == null) {
            status = "No JSON selected. Fallback import folder: " + storage.importsDirectory();
            return;
        }
        try {
            ImportResult result = DatabaseImportService.importFrom(
                    source,
                    storage,
                    TradeCompassClient.database(),
                    TradeCompassClient.loadedWorldKey(),
                    TradeCompassClient.modVersion(),
                    ImportConflictStrategy.MERGE
            );
            TradeCompassClient.replaceDatabase(result.database());
            status = "Imported " + result.importedVillagers() + " villagers from " + source.getFileName()
                    + "; backup: " + result.backupPath().getFileName();
            refreshStats();
        } catch (DatabaseImportService.ImportValidationException exception) {
            status = exception.getMessage();
        } catch (IOException exception) {
            LOGGER.warn("Failed to import Find My Villager database from {}", source, exception);
            status = "Import failed. Current data was not changed.";
        }
    }

    private void backupNow() {
        TradeCompassStorage storage = TradeCompassClient.storage();
        if (storage == null) {
            status = "Storage is not ready.";
            return;
        }
        try {
            Path backupPath = DatabaseBackupService.backupNow(storage, TradeCompassClient.database(), TradeCompassClient.modVersion());
            status = "Backup created: " + backupPath.getFileName();
            refreshStats();
        } catch (IOException exception) {
            LOGGER.warn("Failed to back up Find My Villager database", exception);
            status = "Backup failed. See the game log for details.";
        }
    }

    private void resetCurrentDatabase() {
        long now = System.currentTimeMillis();
        if (now >= resetPendingUntil) {
            resetPendingUntil = now + 5000L;
            if (resetButton != null) {
                resetButton.setMessage(Component.literal("Confirm Reset"));
            }
            status = "Reset clears only the active world/server database. Click again to confirm.";
            return;
        }
        resetPendingUntil = 0L;
        TradeCompassClient.resetCurrentDatabase();
        if (resetButton != null) {
            resetButton.setMessage(Component.literal("Reset Current Database"));
        }
        status = "Current database reset.";
        refreshStats();
    }

    private Path chooseImportPath(TradeCompassStorage storage) {
        Path selected = chooseWithFileDialog(storage);
        if (selected != null) {
            return selected;
        }
        try {
            Files.createDirectories(storage.importsDirectory());
        } catch (IOException exception) {
            LOGGER.warn("Failed to create Find My Villager import fallback directory {}", storage.importsDirectory(), exception);
        }
        return DatabaseImportService.latestFallbackImport(storage).orElse(null);
    }

    private Path chooseWithFileDialog(TradeCompassStorage storage) {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        Frame frame = new Frame("Import Find My Villager Database");
        try {
            FileDialog dialog = new FileDialog(frame, "Import Find My Villager Database", FileDialog.LOAD);
            dialog.setDirectory(storage.importsDirectory().toString());
            dialog.setFile("*.json");
            dialog.setVisible(true);
            if (dialog.getFile() == null) {
                return null;
            }
            return Path.of(dialog.getDirectory(), dialog.getFile());
        } catch (RuntimeException exception) {
            LOGGER.warn("Native file chooser failed; falling back to import directory", exception);
            return null;
        } finally {
            frame.dispose();
        }
    }

    private void refreshStats() {
        stats = DatabaseStats.from(TradeCompassClient.storage(), TradeCompassClient.database());
    }
}
