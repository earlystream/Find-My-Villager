package com.earlystream.tradecompass.gui;

import com.earlystream.tradecompass.TradeCompassClient;
import com.earlystream.tradecompass.data.GroupedSearchResult;
import com.earlystream.tradecompass.data.MerchantRecord;
import com.earlystream.tradecompass.data.SearchResult;
import com.earlystream.tradecompass.data.TradeSearchIndex;
import com.earlystream.tradecompass.data.TradeOfferRecord;
import com.earlystream.tradecompass.util.TimeTextUtil;
import com.earlystream.tradecompass.util.WorldKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeCompassScreen extends Screen {
    private static final int ROW_HEIGHT = 92;
    private EditBox searchBox;
    private final List<GroupedSearchResult> results = new ArrayList<>();
    private final Map<String, LivingEntity> mugshotEntities = new HashMap<>();
    private int scrollOffset;
    private int selectedIndex = -1;

    public TradeCompassScreen() {
        super(Component.literal(TradeCompassClient.modName()));
    }

    @Override
    protected void init() {
        searchBox = new EditBox(font, 18, 24, Math.max(120, width - 220), 20, Component.literal("Search trades"));
        searchBox.setResponder(value -> refreshResults());
        addRenderableWidget(searchBox);
        addRenderableWidget(Button.builder(Component.literal("Clear Target"), button -> TradeCompassClient.clearSelected()).bounds(width - 190, 24, 82, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Delete"), button -> deleteSelected()).bounds(width - 102, 24, 42, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Clear All"), button -> clearAll()).bounds(width - 54, 24, 36, 20).build());
        setInitialFocus(searchBox);
        refreshResults();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xD0101010);
        graphics.drawString(font, title, 18, 10, 0xFFFFFF, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderRows(graphics, mouseX, mouseY);
        renderDetails(graphics);
        graphics.drawString(font, "Saved villagers: " + TradeCompassClient.database().size() + " | Results: " + results.size() + " | World: " + TradeCompassClient.loadedWorldKey(), 18, height - 16, 0xA0A0A0, false);
    }

    private void renderRows(GuiGraphics graphics, int mouseX, int mouseY) {
        int top = 54;
        int bottom = height - 24;
        int listRight = listRight();
        if (results.isEmpty()) {
            String message = TradeCompassClient.database().size() == 0 ? "No saved villager trades for this world yet" : "No saved trades match this search";
            graphics.drawString(font, message, 24, top + 14, 0xFFFFFF, false);
            graphics.drawString(font, "Open a villager trade screen once, then reopen Find My Villager.", 24, top + 30, 0xA0A0A0, false);
            return;
        }
        int visibleRows = Math.max(1, (bottom - top) / ROW_HEIGHT);
        long now = System.currentTimeMillis();
        for (int row = 0; row < visibleRows; row++) {
            int resultIndex = row + scrollOffset;
            if (resultIndex >= results.size()) {
                return;
            }
            GroupedSearchResult result = results.get(resultIndex);
            int y = top + row * ROW_HEIGHT;
            boolean hovered = mouseX >= 14 && mouseX <= listRight && mouseY >= y && mouseY < y + ROW_HEIGHT - 4;
            int fill = resultIndex == selectedIndex ? 0x804A6FA5 : hovered ? 0x602A2F38 : 0x40202020;
            graphics.fill(14, y, listRight, y + ROW_HEIGHT - 4, fill);
            renderRowText(graphics, result, y, now);
            renderAvatar(graphics, result.merchant(), 24, y + 10, 40);
        }
    }

    private void renderRowText(GuiGraphics graphics, GroupedSearchResult result, int y, long now) {
        MerchantRecord merchant = result.merchant();
        int textX = 74;
        String footer = merchant.coordinatesText() + " - checked " + TimeTextUtil.ago(merchant.lastScannedEpochMillis(), now);
        graphics.drawString(font, merchant.professionLevelText(), textX, y + 6, 0xB8D7FF, false);
        graphics.drawString(font, merchant.levelProgressText() + " - " + distanceText(result), textX, y + 19, 0xD0D0D0, false);
        graphics.drawString(font, result.matchingOffers().size() + " matching trade" + (result.matchingOffers().size() == 1 ? "" : "s"), textX, y + 32, 0xFFFFFF, false);
        int tradeY = y + 48;
        int shownTrades = Math.min(1, result.matchingOffers().size());
        for (int i = 0; i < shownTrades; i++) {
            TradeOfferRecord offer = result.matchingOffers().get(i);
            int color = offer.inStock() ? 0xD8FFD8 : 0xFFB0A0;
            graphics.drawString(font, tradeSummary(offer), textX + 8, tradeY, color, false);
            graphics.drawString(font, offer.stockText(), textX + 8, tradeY + 11, 0xA8A8A8, false);
        }
        if (result.matchingOffers().size() > shownTrades) {
            graphics.drawString(font, "+" + (result.matchingOffers().size() - shownTrades) + " more", textX + 120, tradeY + 11, 0xA0A0A0, false);
        }
        graphics.drawString(font, footer, textX, y + 79, 0x909090, false);
    }

    private void renderDetails(GuiGraphics graphics) {
        int left = listRight() + 10;
        if (left > width - 170) {
            return;
        }
        int right = width - 14;
        int top = 54;
        graphics.fill(left, top, right, height - 24, 0x50202020);
        GroupedSearchResult selected = selectedIndex >= 0 && selectedIndex < results.size() ? results.get(selectedIndex) : null;
        if (selected == null) {
            graphics.drawString(font, "Select a villager trade", left + 12, top + 12, 0xFFFFFF, false);
            graphics.drawString(font, "The target panel shows every trade saved from the last check.", left + 12, top + 28, 0xA0A0A0, false);
            return;
        }
        MerchantRecord merchant = selected.merchant();
        long now = System.currentTimeMillis();
        graphics.drawString(font, merchant.professionLevelText(), left + 70, top + 12, 0xFFFFFF, false);
        graphics.drawString(font, merchant.levelProgressText(), left + 70, top + 26, 0xD0D0D0, false);
        renderLevelProgressBar(graphics, merchant, left + 70, top + 40, Math.max(60, right - left - 86));
        graphics.drawString(font, merchant.status(now) + " - " + distanceText(selected), left + 70, top + 52, 0xB8D7FF, false);
        graphics.drawString(font, merchant.coordinatesText(), left + 12, top + 66, 0xC8C8C8, false);
        graphics.drawString(font, "Last checked: " + TimeTextUtil.ago(merchant.lastScannedEpochMillis(), now), left + 12, top + 80, 0xA8A8A8, false);
        renderAvatar(graphics, merchant, left + 12, top + 12, 48);
        graphics.drawString(font, "Trades last checked", left + 12, top + 106, 0xFFFFFF, false);
        int y = top + 122;
        int maxTrades = Math.max(1, (height - y - 30) / 52);
        for (int i = 0; i < merchant.offers().size() && i < maxTrades; i++) {
            TradeOfferRecord offer = merchant.offers().get(i);
            int color = offer.inStock() ? 0xCFE8CF : 0xB87878;
            graphics.drawString(font, offer.displayOutputName(), left + 18, y, 0xFFFFFF, false);
            graphics.drawString(font, offer.priceText(), left + 18, y + 10, 0xD0D0D0, false);
            graphics.drawString(font, offer.stockText(), left + 18, y + 20, color, false);
            graphics.drawString(font, offer.availableOutputText(), left + 18, y + 30, 0xB8B8B8, false);
            graphics.drawString(font, offer.restockText(), left + 18, y + 40, color, false);
            y += 52;
        }
        if (merchant.offers().size() > maxTrades) {
            graphics.drawString(font, "+" + (merchant.offers().size() - maxTrades) + " more trades", left + 18, y, 0xA0A0A0, false);
        }
    }

    private void renderAvatar(GuiGraphics graphics, MerchantRecord merchant, int x, int y, int size) {
        graphics.fill(x, y, x + size, y + size, 0xFF171B1F);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF2B332E);
        LivingEntity mugshot = mugshotEntity(merchant);
        if (mugshot != null) {
            graphics.enableScissor(x + 2, y + 2, x + size - 2, y + size - 2);
            try {
                int renderScale = Math.max(58, Math.round(size * 1.42F));
                int verticalNudge = 4;
                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        graphics,
                        x - size,
                        y - size - verticalNudge,
                        x + size * 2,
                        y + size * 2 - verticalNudge,
                        renderScale,
                        0.55F,
                        x + size / 2.0F,
                        y + size / 2.0F,
                        mugshot
                );
            } catch (Exception ignored) {
            } finally {
                graphics.disableScissor();
            }
            graphics.renderOutline(x, y, size, size, 0x8068A36F);
            return;
        }
        int robeColor = professionColor(merchant.profession());
        graphics.fill(x, y, x + size, y + size, 0xFF2D241B);
        graphics.fill(x + 4, y + 4, x + size - 4, y + size - 4, 0xFFC9956B);
        graphics.fill(x + 6, y + size - 12, x + size - 6, y + size - 4, robeColor);
        graphics.fill(x + 10, y + 14, x + 14, y + 18, 0xFF2A1A12);
        graphics.fill(x + size - 14, y + 14, x + size - 10, y + 18, 0xFF2A1A12);
        graphics.fill(x + size / 2 - 3, y + 18, x + size / 2 + 3, y + 26, 0xFF8F5F3E);
        String initial = merchant.profession().isBlank() ? "?" : merchant.profession().substring(0, 1);
        graphics.drawString(font, initial, x + size / 2 - 3, y + size - 12, 0xFFFFFFFF, false);
    }

    private LivingEntity mugshotEntity(MerchantRecord merchant) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return null;
        }
        String cacheKey = merchant.entityTypeId() + "|" + merchant.professionId() + "|" + merchant.levelNumber() + "|" + merchant.profession();
        return mugshotEntities.computeIfAbsent(cacheKey, key -> createMugshotEntity(client, merchant));
    }

    private LivingEntity createMugshotEntity(Minecraft client, MerchantRecord merchant) {
        if ("minecraft:wandering_trader".equals(merchant.entityTypeId()) || "Wandering Trader".equalsIgnoreCase(merchant.profession())) {
            return EntityType.WANDERING_TRADER.create(client.level, EntitySpawnReason.LOAD);
        }
        Villager villager = EntityType.VILLAGER.create(client.level, EntitySpawnReason.LOAD);
        if (villager == null) {
            return null;
        }
        Holder<VillagerType> villagerType = BuiltInRegistries.VILLAGER_TYPE.get(Identifier.withDefaultNamespace("plains"))
                .or(() -> BuiltInRegistries.VILLAGER_TYPE.getAny())
                .orElse(null);
        String professionId = merchant.professionId().isBlank() ? "minecraft:none" : merchant.professionId();
        Holder<VillagerProfession> profession = BuiltInRegistries.VILLAGER_PROFESSION.get(Identifier.parse(professionId))
                .or(() -> BuiltInRegistries.VILLAGER_PROFESSION.get(Identifier.withDefaultNamespace("none")))
                .or(() -> BuiltInRegistries.VILLAGER_PROFESSION.getAny())
                .orElse(null);
        int level = merchant.levelNumber() > 0 ? merchant.levelNumber() : levelNumberFromText(merchant.level());
        if (villagerType != null && profession != null) {
            villager.setVillagerData(new VillagerData(villagerType, profession, Math.max(1, Math.min(5, level))));
        }
        villager.setYRot(180.0F);
        villager.setYBodyRot(180.0F);
        villager.setXRot(0.0F);
        return villager;
    }

    private int levelNumberFromText(String level) {
        if ("Apprentice".equalsIgnoreCase(level)) {
            return 2;
        }
        if ("Journeyman".equalsIgnoreCase(level)) {
            return 3;
        }
        if ("Expert".equalsIgnoreCase(level)) {
            return 4;
        }
        if ("Master".equalsIgnoreCase(level)) {
            return 5;
        }
        return 1;
    }

    private int professionColor(String profession) {
        int hash = profession == null ? 0 : profession.hashCode();
        int r = 80 + Math.abs(hash & 0x5F);
        int g = 80 + Math.abs((hash >> 8) & 0x5F);
        int b = 80 + Math.abs((hash >> 16) & 0x5F);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int listRight() {
        return Math.min(width - 190, Math.max(360, (int) (width * 0.62F)));
    }

    private void renderLevelProgressBar(GuiGraphics graphics, MerchantRecord merchant, int x, int y, int width) {
        int percent = merchant.levelProgressPercent();
        if (percent < 0 || percent >= 100 || merchant.levelNumber() >= 5) {
            return;
        }
        graphics.fill(x, y, x + width, y + 6, 0xFF252A30);
        graphics.fill(x, y, x + Math.max(1, width * percent / 100), y + 6, 0xFF6EA86E);
    }

    private String tradeSummary(TradeOfferRecord offer) {
        String output = offer.output().count() > 1
                ? offer.output().count() + "x " + offer.displayOutputName()
                : offer.displayOutputName();
        String stock = offer.inStock() ? "" : " [OUT OF STOCK]";
        return offer.priceText() + " -> " + output + stock;
    }

    private String distanceText(GroupedSearchResult result) {
        if (result.merchant().unknownPosition()) {
            return "Unknown position";
        }
        return result.distance() < 0 ? "Different dimension" : Math.round(result.distance()) + "m away";
    }

    private void refreshResults() {
        Minecraft client = Minecraft.getInstance();
        String dimension = WorldKeyUtil.currentDimension(client);
        double x = client.player == null ? 0.0D : client.player.getX();
        double y = client.player == null ? 0.0D : client.player.getY();
        double z = client.player == null ? 0.0D : client.player.getZ();
        results.clear();
        results.addAll(TradeSearchIndex.searchGrouped(TradeCompassClient.database(), searchBox == null ? "" : searchBox.getValue(), dimension, x, y, z));
        scrollOffset = Math.min(scrollOffset, Math.max(0, results.size() - 1));
        selectedIndex = results.isEmpty() ? -1 : 0;
        selectCurrentResult();
    }

    private void selectCurrentResult() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return;
        }
        GroupedSearchResult result = results.get(selectedIndex);
        TradeOfferRecord primaryOffer = result.primaryOffer();
        if (primaryOffer != null) {
            TradeCompassClient.select(new SearchResult(result.merchant(), primaryOffer, result.distance()));
        }
    }

    private void deleteSelected() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return;
        }
        TradeCompassClient.database().delete(results.get(selectedIndex).merchant().merchantKey());
        TradeCompassClient.clearSelected();
        TradeCompassClient.save();
        refreshResults();
    }

    private void clearAll() {
        TradeCompassClient.database().clear();
        TradeCompassClient.clearSelected();
        TradeCompassClient.save();
        refreshResults();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        int index = event.x() <= listRight() ? rowIndex(event.y()) : -1;
        if (index >= 0 && index < results.size()) {
            selectedIndex = index;
            selectCurrentResult();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int max = Math.max(0, results.size() - Math.max(1, (height - 78) / ROW_HEIGHT));
        scrollOffset = Math.max(0, Math.min(max, scrollOffset - (int) Math.signum(verticalAmount)));
        return true;
    }

    private int rowIndex(double mouseY) {
        int top = 54;
        if (mouseY < top || mouseY > height - 24) {
            return -1;
        }
        return ((int) mouseY - top) / ROW_HEIGHT + scrollOffset;
    }
}
