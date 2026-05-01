package com.earlystream.tradecompass.gui;

import com.earlystream.tradecompass.TradeCompassMod;
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
import net.minecraft.client.input.KeyEvent;
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

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeCompassScreen extends Screen {
    private static final int ROW_HEIGHT = 92;
    private static final String STAR_EMPTY = "☆";
    private static final String STAR_FILLED = "★";
    private static final int STAR_GOLD = 0xFFFFD45A;
    private static final int STAR_EMPTY_COLOR = 0xFF8A8A8A;
    private static String lastSelectedMerchantKey = null;
    private static int lastSelectedTradeIndex = 0;
    private EditBox searchBox;
    private EditBox nameBox;
    private Button clearAllButton;
    private Button toggleNamesButton;
    private Button saveNameButton;
    private Button clearNameButton;
    private String nameBoxMerchantKey = "";
    private long clearPendingUntil = 0;
    private final List<GroupedSearchResult> results = new ArrayList<>();
    private final Map<String, LivingEntity> mugshotEntities = new HashMap<>();
    private int scrollOffset;
    private int selectedIndex = -1;
    private int selectedTradeIndex = 0;
    private int detailsScrollOffset = 0;

    public TradeCompassScreen() {
        super(Component.literal(TradeCompassMod.modName()));
    }

    @Override
    protected void init() {
        searchBox = new EditBox(font, 18, 24, Math.max(120, width - 352), 20, Component.literal("Search trades"));
        searchBox.setResponder(value -> refreshResults());
        addRenderableWidget(searchBox);
        addRenderableWidget(Button.builder(Component.literal("Data"), button -> Minecraft.getInstance().setScreen(new DataManagerScreen(this))).bounds(width - 322, 24, 56, 20).build());
        toggleNamesButton = addRenderableWidget(Button.builder(Component.empty(), button -> toggleVillagerNames()).bounds(width - 260, 24, 64, 20).build());
        updateToggleNamesButton();
        addRenderableWidget(Button.builder(Component.literal("Clear Target"), button -> TradeCompassMod.clearSelected()).bounds(width - 190, 24, 82, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Delete"), button -> deleteSelected()).bounds(width - 102, 24, 42, 20).build());
        clearAllButton = addRenderableWidget(Button.builder(Component.literal("Clear All"), button -> {
            long now = System.currentTimeMillis();
            if (now < clearPendingUntil) {
                clearPendingUntil = 0;
                clearAllButton.setMessage(Component.literal("Clear All"));
                clearAll();
            } else {
                clearPendingUntil = now + 3000;
                clearAllButton.setMessage(Component.literal("Sure?"));
            }
        }).bounds(width - 54, 24, 36, 20).build());
        int nameControlsY = 150;
        int nameControlsRight = width - 14;
        int nameControlsLeft = listRight() + 92;
        nameBox = addRenderableWidget(new EditBox(font, nameControlsLeft, nameControlsY, Math.max(70, nameControlsRight - nameControlsLeft - 104), 20, Component.literal("Villager name")));
        nameBox.setMaxLength(80);
        saveNameButton = addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveManualName()).bounds(nameControlsRight - 98, nameControlsY, 44, 20).build());
        clearNameButton = addRenderableWidget(Button.builder(Component.literal("Clear"), button -> clearManualName()).bounds(nameControlsRight - 50, nameControlsY, 36, 20).build());
        setNameControlsVisible(false);
        setInitialFocus(searchBox);
        refreshResults();
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (clearPendingUntil > 0 && System.currentTimeMillis() > clearPendingUntil) {
            clearPendingUntil = 0;
            clearAllButton.setMessage(Component.literal("Clear All"));
        }
        graphics.fill(0, 0, width, height, 0xD0101010);
        graphics.fill(0, 0, width, 47, 0x18FFFFFF);
        graphics.drawString(font, title, 18, 10, 0xFFFFFFFF, false);
        graphics.fill(0, 47, width, 48, 0x60FFFFFF);
        graphics.fill(0, height - 23, width, height - 22, 0x40FFFFFF);
        renderRows(graphics, mouseX, mouseY);
        renderDetails(graphics);
        graphics.drawString(font, "Saved: " + TradeCompassMod.database().size() + " villagers  |  Results: " + results.size() + "  |  " + TradeCompassMod.loadedWorldKey(), 18, height - 16, 0xFF707070, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderRows(GuiGraphics graphics, int mouseX, int mouseY) {
        int top = 54;
        int bottom = height - 24;
        int listRight = listRight();
        if (results.isEmpty()) {
            String message = TradeCompassMod.database().size() == 0 ? "No saved villager trades for this world yet" : "No saved trades match this search";
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
            graphics.fill(14, y, 17, y + ROW_HEIGHT - 4, levelAccentColor(result.merchant().levelNumber()));
            graphics.fill(17, y + ROW_HEIGHT - 5, listRight, y + ROW_HEIGHT - 4, 0x25FFFFFF);
            renderRowText(graphics, result, y, now);
            renderFavoriteStar(graphics, result.merchant(), rowStarX(), y + 8);
            renderAvatar(graphics, result.merchant(), 24, y + 10, 40);
        }
        if (results.size() > visibleRows) {
            int maxScroll = results.size() - visibleRows;
            int barHeight = bottom - top;
            int thumbHeight = Math.max(12, barHeight * visibleRows / results.size());
            int thumbY = top + (barHeight - thumbHeight) * scrollOffset / maxScroll;
            graphics.fill(listRight - 3, top, listRight - 1, bottom, 0x28FFFFFF);
            graphics.fill(listRight - 3, thumbY, listRight - 1, thumbY + thumbHeight, 0xA0909090);
        }
    }

    private void renderRowText(GuiGraphics graphics, GroupedSearchResult result, int y, long now) {
        MerchantRecord merchant = result.merchant();
        int textX = 74;

        graphics.drawString(font, resultTitle(result), textX, y + 6, 0xFFB8D7FF, false);
        graphics.drawString(font, merchant.levelProgressText() + "  -  " + distanceText(result), textX, y + 19, 0xFF888888, false);

        int matchCount = result.matchingOffers().size();
        String matchText = matchCount <= 1
                ? matchCount + " matching trade" + (matchCount == 1 ? "" : "s")
                : "1 of " + matchCount + " matching trades";
        graphics.drawString(font, matchText, textX, y + 32, 0xFFAAAAAA, false);

        int dotX = textX + font.width(matchText) + 6;
        for (int i = 0; i < Math.min(matchCount, 12); i++) {
            graphics.fill(dotX, y + 34, dotX + 4, y + 39,
                    result.matchingOffers().get(i).inStock() ? 0xFF3D8A4A : 0xFF8A3D3D);
            dotX += 6;
        }

        graphics.fill(textX, y + 43, listRight() - 8, y + 44, 0x22FFFFFF);

        int tradeY = y + 48;
        if (matchCount > 0) {
            TradeOfferRecord offer = result.matchingOffers().get(0);
            int nameColor = offer.inStock() ? 0xFFBBEEBB : 0xFFEEBBBB;
            graphics.drawString(font, offer.displayOutputName(), textX + 4, tradeY, nameColor, false);
            graphics.drawString(font, offer.priceText(), textX + 4, tradeY + 11, 0xFF666666, false);
            if (matchCount > 1) {
                graphics.drawString(font, "+" + (matchCount - 1) + " more", textX + 4 + Math.max(95, font.width(offer.priceText()) + 12), tradeY + 11, 0xFF777777, false);
            }
        }

        graphics.drawString(font, merchant.coordinatesText() + "  -  " + TimeTextUtil.ago(merchant.lastScannedEpochMillis(), now), textX, y + 79, 0xFF555555, false);
    }

    private void renderDetails(GuiGraphics graphics) {
        int left = listRight() + 10;
        if (left > width - 170) {
            setNameControlsVisible(false);
            return;
        }
        int right = width - 14;
        int top = 54;
        layoutNameControls(left, right, top + 96);
        graphics.fill(left - 5, 48, left - 4, height - 23, 0x40FFFFFF);
        graphics.fill(left, top, right, height - 24, 0x40202020);
        GroupedSearchResult selected = selectedIndex >= 0 && selectedIndex < results.size() ? results.get(selectedIndex) : null;
        if (selected == null) {
            setNameControlsVisible(false);
            graphics.drawString(font, "Select a villager", left + 12, top + 20, 0xFFFFFFFF, false);
            graphics.drawString(font, "Click a row to see all of its trades here.", left + 12, top + 36, 0xFF808080, false);
            return;
        }
        MerchantRecord merchant = selected.merchant();
        syncNameControls(merchant);
        long now = System.currentTimeMillis();
        graphics.fill(left, top, right, top + 126, 0x20FFFFFF);
        graphics.drawString(font, detailsTitle(merchant), left + 70, top + 12, 0xFFFFFFFF, false);
        renderFavoriteStar(graphics, merchant, right - 24, top + 10);
        graphics.drawString(font, merchant.levelProgressText(), left + 70, top + 26, 0xFFD0D0D0, false);
        renderLevelProgressBar(graphics, merchant, left + 70, top + 40, Math.max(60, right - left - 86));
        graphics.drawString(font, merchant.status(now) + " - " + distanceText(selected), left + 70, top + 52, 0xFFB8D7FF, false);
        graphics.drawString(font, merchant.coordinatesText(), left + 12, top + 66, 0xFFC8C8C8, false);
        graphics.drawString(font, "Last checked: " + TimeTextUtil.ago(merchant.lastScannedEpochMillis(), now), left + 12, top + 80, 0xFFA8A8A8, false);
        graphics.drawString(font, "Name", left + 12, top + 102, 0xFFA8A8A8, false);
        renderAvatar(graphics, merchant, left + 12, top + 12, 48);
        graphics.fill(left + 8, top + 126, right - 8, top + 127, 0x50FFFFFF);
        int inStockCount = 0;
        for (TradeOfferRecord t : merchant.offers()) {
            if (t.inStock()) {
                inStockCount++;
            }
        }
        int outCount = merchant.offers().size() - inStockCount;
        String tradesLabel = merchant.offers().size() + " trades";
        if (outCount > 0) {
            tradesLabel += "  -  " + inStockCount + " in  -  " + outCount + " out";
        } else {
            tradesLabel += "  -  all in stock";
        }
        graphics.drawString(font, tradesLabel, left + 12, top + 132, 0xFF909090, false);
        int listTop = detailsTradeListTop();
        int listBottom = height - 24;
        int maxVisible = Math.max(1, (listBottom - listTop) / 36);
        int totalTrades = merchant.offers().size();
        int maxScroll = Math.max(0, totalTrades - maxVisible);
        detailsScrollOffset = Math.max(0, Math.min(maxScroll, detailsScrollOffset));
        graphics.enableScissor(left, listTop, right, listBottom);
        try {
            int y = listTop;
            for (int i = detailsScrollOffset; i < totalTrades && y < listBottom + 34; i++) {
                TradeOfferRecord offer = merchant.offers().get(i);
                int nameColor = offer.inStock() ? 0xFFFFFFFF : 0xFFCC9999;
                int statusColor = offer.inStock() ? 0xFF6DB86D : 0xFF997070;
                int stockBar = offer.inStock() ? 0xFF3A8A4A : 0xFF8A3A3A;
                String status = offer.inStock()
                        ? offer.remainingUses() + " / " + offer.maxUses() + " uses left"
                        : "Sold out";
                int cardFill = (i == selectedTradeIndex) ? 0x40FFFFFF : 0x15FFFFFF;
                graphics.fill(left + 12, y, right - 8, y + 34, cardFill);
                graphics.fill(left + 12, y, left + 14, y + 34, stockBar);
                graphics.drawString(font, offer.displayOutputName(), left + 18, y + 2, nameColor, false);
                graphics.drawString(font, offer.priceText(), left + 18, y + 13, 0xFF909090, false);
                graphics.drawString(font, status, left + 18, y + 24, statusColor, false);
                y += 36;
            }
        } finally {
            graphics.disableScissor();
        }
        if (maxScroll > 0) {
            int barHeight = listBottom - listTop;
            int thumbHeight = Math.max(10, barHeight * maxVisible / totalTrades);
            int thumbY = listTop + (barHeight - thumbHeight) * detailsScrollOffset / maxScroll;
            graphics.fill(right - 4, listTop, right - 2, listBottom, 0x30FFFFFF);
            graphics.fill(right - 4, thumbY, right - 2, thumbY + thumbHeight, 0xA0AAAAAA);
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

    private int levelAccentColor(int level) {
        return switch (level) {
            case 2 -> 0xFF4E9E4E;
            case 3 -> 0xFF4E7EC0;
            case 4 -> 0xFFC07830;
            case 5 -> 0xFFC0A030;
            default -> 0xFF706050;
        };
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

    private int rowStarX() {
        return listRight() - 22;
    }

    private void renderFavoriteStar(GuiGraphics graphics, MerchantRecord merchant, int x, int y) {
        String star = merchant.favorite() ? STAR_FILLED : STAR_EMPTY;
        int color = merchant.favorite() ? STAR_GOLD : STAR_EMPTY_COLOR;
        graphics.drawString(font, star, x, y, color, false);
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
        results.addAll(TradeSearchIndex.searchGrouped(
                TradeCompassMod.database(),
                searchBox == null ? "" : searchBox.getValue(),
                dimension,
                x,
                y,
                z,
                TradeCompassMod.settings().showVillagerNamesInSearch()
        ));
        scrollOffset = Math.min(scrollOffset, Math.max(0, results.size() - 1));
        selectedIndex = results.isEmpty() ? -1 : 0;
        selectedTradeIndex = 0;
        detailsScrollOffset = 0;
        if (lastSelectedMerchantKey != null) {
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).merchant().merchantKey().equals(lastSelectedMerchantKey)) {
                    selectedIndex = i;
                    selectedTradeIndex = lastSelectedTradeIndex;
                    int visibleRows = Math.max(1, (height - 78) / ROW_HEIGHT);
                    scrollOffset = Math.max(0, selectedIndex - visibleRows / 2);
                    detailsScrollOffset = Math.max(0, selectedTradeIndex - 2);
                    break;
                }
            }
        }
        selectCurrentResult();
        targetSelectedTrade();
    }

    private void selectCurrentResult() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return;
        }
        GroupedSearchResult result = results.get(selectedIndex);
        lastSelectedMerchantKey = result.merchant().merchantKey();
        List<TradeOfferRecord> offers = result.merchant().offers();
        if (selectedTradeIndex >= offers.size()) {
            selectedTradeIndex = 0;
        }
        lastSelectedTradeIndex = selectedTradeIndex;
    }

    private void targetSelectedTrade() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return;
        }
        GroupedSearchResult result = results.get(selectedIndex);
        List<TradeOfferRecord> offers = result.merchant().offers();
        if (offers.isEmpty()) {
            return;
        }
        TradeOfferRecord offer = selectedTradeIndex < offers.size() ? offers.get(selectedTradeIndex) : offers.get(0);
        TradeCompassMod.select(new SearchResult(result.merchant(), offer, result.distance()));
    }

    private void deleteSelected() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return;
        }
        TradeCompassMod.database().delete(results.get(selectedIndex).merchant().merchantKey());
        TradeCompassMod.clearSelected();
        TradeCompassMod.save();
        refreshResults();
    }

    private void clearAll() {
        TradeCompassMod.database().clear();
        TradeCompassMod.clearSelected();
        TradeCompassMod.save();
        refreshResults();
    }

    private void toggleVillagerNames() {
        boolean enabled = !TradeCompassMod.settings().showVillagerNamesInSearch();
        TradeCompassMod.settings().showVillagerNamesInSearch(enabled);
        TradeCompassMod.saveSettings();
        updateToggleNamesButton();
        refreshResults();
    }

    private void updateToggleNamesButton() {
        if (toggleNamesButton != null) {
            toggleNamesButton.setMessage(Component.literal(TradeCompassMod.settings().showVillagerNamesInSearch() ? "Names: On" : "Names: Off"));
        }
    }

    private void saveManualName() {
        MerchantRecord merchant = selectedMerchant();
        if (merchant == null || nameBox == null) {
            return;
        }
        merchant.manualName(nameBox.getValue());
        TradeCompassMod.save();
        refreshResults();
    }

    private void clearManualName() {
        MerchantRecord merchant = selectedMerchant();
        if (merchant == null) {
            return;
        }
        merchant.manualName("");
        if (nameBox != null) {
            nameBox.setValue("");
        }
        TradeCompassMod.save();
        refreshResults();
    }

    private MerchantRecord selectedMerchant() {
        if (selectedIndex < 0 || selectedIndex >= results.size()) {
            return null;
        }
        return results.get(selectedIndex).merchant();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        int left = listRight() + 10;
        int right = width - 14;
        int listTop = detailsTradeListTop();
        int listBottom = height - 24;
        if (selectedIndex >= 0 && selectedIndex < results.size() && detailsStarHit(event.x(), event.y(), left, right)) {
            toggleFavorite(results.get(selectedIndex).merchant());
            return true;
        }
        if (event.x() > left && event.x() < right && event.y() >= listTop && event.y() < listBottom
                && selectedIndex >= 0 && selectedIndex < results.size()) {
            int tradeIndex = detailsScrollOffset + ((int) event.y() - listTop) / 36;
            MerchantRecord merchant = results.get(selectedIndex).merchant();
            if (tradeIndex >= 0 && tradeIndex < merchant.offers().size()) {
                selectedTradeIndex = tradeIndex;
                lastSelectedTradeIndex = tradeIndex;
                lastSelectedMerchantKey = merchant.merchantKey();
                TradeOfferRecord offer = merchant.offers().get(tradeIndex);
                TradeCompassMod.select(new SearchResult(merchant, offer, results.get(selectedIndex).distance()));
                return true;
            }
        }
        int index = event.x() <= listRight() ? rowIndex(event.y()) : -1;
        if (index >= 0 && index < results.size()) {
            if (rowStarHit(event.x(), event.y(), index)) {
                selectedIndex = index;
                selectedTradeIndex = 0;
                detailsScrollOffset = 0;
                toggleFavorite(results.get(index).merchant());
                return true;
            }
            if (index != selectedIndex) {
                selectedTradeIndex = 0;
                detailsScrollOffset = 0;
            }
            selectedIndex = index;
            selectCurrentResult();
            targetSelectedTrade();
            return true;
        }
        return false;
    }

    private boolean rowStarHit(double mouseX, double mouseY, int resultIndex) {
        int row = resultIndex - scrollOffset;
        if (row < 0) {
            return false;
        }
        int y = 54 + row * ROW_HEIGHT;
        int x = rowStarX();
        return mouseX >= x - 6 && mouseX <= x + 16 && mouseY >= y + 2 && mouseY <= y + 24;
    }

    private boolean detailsStarHit(double mouseX, double mouseY, int left, int right) {
        if (left > width - 170) {
            return false;
        }
        int x = right - 24;
        int y = 54 + 10;
        return mouseX >= x - 6 && mouseX <= x + 16 && mouseY >= y - 4 && mouseY <= y + 18;
    }

    private void toggleFavorite(MerchantRecord merchant) {
        if (merchant == null) {
            return;
        }
        merchant.toggleFavorite();
        lastSelectedMerchantKey = merchant.merchantKey();
        TradeCompassMod.save();
        refreshResults();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int dir = (int) Math.signum(verticalAmount);
        if (mouseX > listRight() && selectedIndex >= 0 && selectedIndex < results.size()) {
            int totalTrades = results.get(selectedIndex).merchant().offers().size();
            int maxVisible = Math.max(1, (height - 24 - detailsTradeListTop()) / 36);
            detailsScrollOffset = Math.max(0, Math.min(Math.max(0, totalTrades - maxVisible), detailsScrollOffset - dir));
        } else {
            scroll(-dir * 3);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (nameBox != null && nameBox.isFocused() && super.keyPressed(event)) {
            return true;
        }
        int visibleRows = Math.max(1, (height - 78) / ROW_HEIGHT);
        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_UP) {
            scroll(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            scroll(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            scroll(-visibleRows);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            scroll(visibleRows);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            scrollOffset = 0;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            scrollOffset = Math.max(0, results.size() - Math.max(1, visibleRows));
            return true;
        }
        return super.keyPressed(event);
    }

    private void scroll(int delta) {
        int max = Math.max(0, results.size() - Math.max(1, (height - 78) / ROW_HEIGHT));
        scrollOffset = Math.max(0, Math.min(max, scrollOffset + delta));
    }

    private int rowIndex(double mouseY) {
        int top = 54;
        if (mouseY < top || mouseY > height - 24) {
            return -1;
        }
        return ((int) mouseY - top) / ROW_HEIGHT + scrollOffset;
    }

    private String resultTitle(GroupedSearchResult result) {
        MerchantRecord merchant = result.merchant();
        if (!TradeCompassMod.settings().showVillagerNamesInSearch() || !merchant.hasVillagerName()) {
            return merchant.professionLevelText();
        }
        return merchant.villagerName() + " — " + merchant.professionLevelText();
    }

    private String detailsTitle(MerchantRecord merchant) {
        if (!TradeCompassMod.settings().showVillagerNamesInSearch() || !merchant.hasVillagerName()) {
            return merchant.professionLevelText();
        }
        return merchant.villagerName() + " — " + merchant.professionLevelText();
    }

    private void layoutNameControls(int left, int right, int y) {
        if (nameBox == null || saveNameButton == null || clearNameButton == null) {
            return;
        }
        int boxLeft = left + 48;
        nameBox.setX(boxLeft);
        nameBox.setY(y);
        nameBox.setWidth(Math.max(70, right - boxLeft - 104));
        saveNameButton.setX(right - 98);
        saveNameButton.setY(y);
        clearNameButton.setX(right - 50);
        clearNameButton.setY(y);
    }

    private void syncNameControls(MerchantRecord merchant) {
        setNameControlsVisible(true);
        if (nameBox == null || merchant == null) {
            return;
        }
        if (!merchant.merchantKey().equals(nameBoxMerchantKey)) {
            nameBoxMerchantKey = merchant.merchantKey();
            nameBox.setValue(merchant.manualName());
        }
        if (clearNameButton != null) {
            clearNameButton.active = !merchant.manualName().isBlank();
        }
    }

    private void setNameControlsVisible(boolean visible) {
        if (nameBox != null) {
            nameBox.visible = visible;
            nameBox.active = visible;
        }
        if (saveNameButton != null) {
            saveNameButton.visible = visible;
            saveNameButton.active = visible;
        }
        if (clearNameButton != null) {
            clearNameButton.visible = visible;
            clearNameButton.active = visible;
        }
        if (!visible) {
            nameBoxMerchantKey = "";
        }
    }

    private int detailsTradeListTop() {
        return 54 + 148;
    }
}
