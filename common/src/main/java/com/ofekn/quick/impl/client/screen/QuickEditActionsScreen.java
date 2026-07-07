package com.ofekn.quick.impl.client.screen;

import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.slot.ContainerSlotKey;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class QuickEditActionsScreen extends Screen {

    private static final int PADDING = 16;
    private static final int CONTENT_W = 340;
    private static final int BTN_H = 20;
    private static final int ITEM_H = 20;
    private static final int GAP = 4;
    private static final int TAB_BOTTOM_GAP = 2;

    private final String[] pendingActions = new String[10];
    private int selectedTab = 0;

    // Layout fields computed in init
    private int contentX;

    @Nullable private ItemStack selectedStack;
    @Nullable private ISlotKey selectedKey;
    @Nullable private String selectedMethod;

    private List<ISlotKey> options = List.of();
    @Nullable private Player player;
    private final ItemStack[] tabDisplayStacks = new ItemStack[10];

    private final Button[] tabButtons = new Button[10];
    private ItemSelectionList itemList;
    private Button byIdButton, byNameButton, bySlotButton;

    public QuickEditActionsScreen() {
        super(Component.translatable("gui.quick.edit_actions"));
        for (int i = 0; i < 10; i++) {
            pendingActions[i] = QuickIntegrations.CONFIG.getActionAssignment(i);
        }
        Arrays.fill(tabDisplayStacks, ItemStack.EMPTY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        encodeCurrentTab();
        super.init();
        player = minecraft.player;
        options = player != null ? QuickClient.getOptions(player) : List.of();

        for (int i = 0; i < 10; i++) {
            tabDisplayStacks[i] = resolveStack(pendingActions[i]);
        }

        contentX = (width - CONTENT_W) / 2;
        buildTabButtons();
        buildEditPanel();
        loadTabState(selectedTab);
    }

    private void buildTabButtons() {
        int buttonWidth = CONTENT_W / 10;
        for (int i = 0; i < 10; i++) {
            final int fi = i;
            int x = contentX + i * buttonWidth;
            tabButtons[i] = addRenderableWidget(
                Button.builder(Component.literal(""), _ -> selectTab(fi))
                    .pos(x, PADDING).size(buttonWidth, BTN_H)
                    .tooltip(Tooltip.create(Component.translatable("gui.quick.edit_actions.tab", fi)))
                    .build()
            );
        }
    }

    private void buildEditPanel() {
        int listY = PADDING + BTN_H + TAB_BOTTOM_GAP;
        int listH = height - 2 * PADDING - 3 * BTN_H - 2 * GAP - TAB_BOTTOM_GAP;
        itemList = addRenderableWidget(
            new ItemSelectionList(
                    minecraft,
                    contentX, CONTENT_W,
                    listH, listY,
                    ITEM_H,
                    options,
                    player
            )
        );

        int methodY = height - PADDING - 2 * BTN_H - GAP;
        int methodWidth = (CONTENT_W - 3 * GAP) / 4;

        byIdButton = addRenderableWidget(
            Button.builder(methodLabel("gui.quick.edit_action.by_id", "id"), _ -> selectMethod("id"))
                .pos(contentX, methodY).size(methodWidth, BTN_H).build()
        );
        byNameButton = addRenderableWidget(
            Button.builder(methodLabel("gui.quick.edit_action.by_name", "name"), _ -> selectMethod("name"))
                .pos(contentX + (methodWidth + GAP), methodY).size(methodWidth, BTN_H).build()
        );
        bySlotButton = addRenderableWidget(
            Button.builder(methodLabel("gui.quick.edit_action.by_slot", "slot"), _ -> selectMethod("slot"))
                .pos(contentX + 2 * (methodWidth + GAP), methodY).size(methodWidth, BTN_H).build()
        );
        bySlotButton.active = false;
        addRenderableWidget(
            Button.builder(Component.translatable("gui.quick.edit_action.clear"), _ -> clearTab())
                .pos(contentX + 3 * (methodWidth + GAP), methodY).size(methodWidth, BTN_H).build()
        );

        int finalY = height - PADDING - BTN_H;
        int finalWidth = (CONTENT_W - GAP) / 2;

        addRenderableWidget(
            Button.builder(Component.translatable("gui.quick.cancel"), _ -> cancel())
                .pos(contentX, finalY).size(finalWidth, BTN_H).build()
        );
        addRenderableWidget(
            Button.builder(Component.translatable("gui.quick.edit_actions.save"), _ -> saveAll())
                .pos(contentX + finalWidth + GAP, finalY).size(finalWidth, BTN_H).build()
        );
    }

    private void selectTab(int newTab) {
        encodeCurrentTab();
        selectedTab = newTab;
        clearWidgets();
        buildTabButtons();
        buildEditPanel();
        loadTabState(selectedTab);
    }

    private void loadTabState(int tab) {
        selectedStack = null;
        selectedKey = null;
        selectedMethod = null;
        itemList.setSelected(null);

        String assignment = pendingActions[tab];
        if (assignment == null || assignment.isEmpty()) {
            refreshWidgets();
            return;
        }

        if (assignment.startsWith("id:")) {
            selectedMethod = "id";
            String id = assignment.substring(3);
            for (ISlotKey key : options) {
                if (player == null) break;
                ItemStack stack = key.get(player);
                if (!stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id)) {
                    selectedStack = stack;
                    selectedKey = key;
                    break;
                }
            }
        } else if (assignment.startsWith("name:")) {
            selectedMethod = "name";
            String name = assignment.substring(5);
            for (ISlotKey key : options) {
                if (player == null) break;
                ItemStack stack = key.get(player);
                if (!stack.isEmpty() && stack.getHoverName().getString().equals(name)) {
                    selectedStack = stack;
                    selectedKey = key;
                    break;
                }
            }
        } else if (assignment.startsWith("slot:")) {
            selectedMethod = "slot";
            try {
                int slotIndex = Integer.parseInt(assignment.substring(5));
                for (ISlotKey key : options) {
                    if (key instanceof ContainerSlotKey csk && csk.index() == slotIndex) {
                        selectedKey = key;
                        if (player != null) selectedStack = key.get(player);
                        break;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (selectedKey != null) {
            itemList.selectByKey(selectedKey);
        }
        refreshWidgets();
    }

    private void encodeCurrentTab() {
        if (selectedStack == null || selectedMethod == null) return;
        String value = switch (selectedMethod) {
            case "id"   -> "id:" + BuiltInRegistries.ITEM.getKey(selectedStack.getItem());
            case "name" -> "name:" + selectedStack.getHoverName().getString();
            case "slot" -> "slot:" + ((ContainerSlotKey) selectedKey).index();
            default     -> throw new IllegalStateException("Unknown method: " + selectedMethod);
        };
        pendingActions[selectedTab] = value;
        tabDisplayStacks[selectedTab] = resolveStack(value);
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        refreshMethodButtons();
    }

    private void refreshMethodButtons() {
        byIdButton.setMessage(methodLabel("gui.quick.edit_action.by_id", "id"));
        byNameButton.setMessage(methodLabel("gui.quick.edit_action.by_name", "name"));
        bySlotButton.setMessage(methodLabel("gui.quick.edit_action.by_slot", "slot"));
    }

    private Component methodLabel(String key, String method) {
        Component base = Component.translatable(key);
        return method.equals(selectedMethod) ? base.copy().withStyle(ChatFormatting.GREEN) : base;
    }

    private void refreshWidgets() {
        bySlotButton.active = selectedKey instanceof ContainerSlotKey;
        if ("slot".equals(selectedMethod) && !(selectedKey instanceof ContainerSlotKey)) {
            selectedMethod = null;
        }
        refreshMethodButtons();
    }

    void onItemSelected(ItemStack stack, ISlotKey key) {
        selectedStack = stack;
        selectedKey = key;
        bySlotButton.active = key instanceof ContainerSlotKey;
        if ("slot".equals(selectedMethod) && !(key instanceof ContainerSlotKey)) {
            selectedMethod = null;
        }
        refreshMethodButtons();
    }

    private void saveAll() {
        encodeCurrentTab();
        for (int i = 0; i < 10; i++) {
            String v = pendingActions[i];
            QuickIntegrations.CONFIG.setActionAssignment(i, v != null ? v : "");
        }
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void cancel() {
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void clearTab() {
        pendingActions[selectedTab] = "";
        tabDisplayStacks[selectedTab] = new ItemStack(Items.BARRIER);
        selectedStack = null;
        selectedKey = null;
        selectedMethod = null;
        itemList.setSelected(null);
        bySlotButton.active = false;
        refreshMethodButtons();
    }

    private ItemStack resolveStack(String assignment) {
        if (assignment == null || assignment.isEmpty()) {
            return new ItemStack(Items.BARRIER);
        }
        if (player == null) return ItemStack.EMPTY;

        if (assignment.startsWith("id:")) {
            String id = assignment.substring(3);
            for (ISlotKey key : options) {
                ItemStack stack = key.get(player);
                if (!stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id)) {
                    return stack;
                }
            }
        } else if (assignment.startsWith("name:")) {
            String name = assignment.substring(5);
            for (ISlotKey key : options) {
                ItemStack stack = key.get(player);
                if (!stack.isEmpty() && stack.getHoverName().getString().equals(name)) {
                    return stack;
                }
            }
        } else if (assignment.startsWith("slot:")) {
            try {
                int slotIndex = Integer.parseInt(assignment.substring(5));
                for (ISlotKey key : options) {
                    if (key instanceof ContainerSlotKey csk && csk.index() == slotIndex) {
                        return key.get(player);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int titleY = PADDING + BTN_H + GAP + 2;
        graphics.centeredText(font, Component.translatable("gui.quick.edit_action", selectedTab), width / 2, titleY, 0xFFFFFF);

        for (int i = 0; i < 10; i++) {
            var btn = tabButtons[i];
            int btnX = btn.getX();
            int btnY = btn.getY();
            int btnW = btn.getWidth();
            int textColor = (i == selectedTab) ? 0xFFFFFF55 : 0xFFFFFFFF;
            graphics.text(font, Component.literal("" + i), btnX + 6, btnY + 6, textColor);
            ItemStack stack = tabDisplayStacks[i];
            graphics.fakeItem(stack, btnX + btnW - 18, btnY + 2);
        }
    }

    class ItemSelectionList extends ObjectSelectionList<ItemSelectionList.ItemEntry> {
        private final int listWidth;

        ItemSelectionList(Minecraft mc, int listX, int listWidth, int height, int top, int itemHeight,
                          List<ISlotKey> keys, @Nullable Player player) {
            super(mc, listWidth, height, top, itemHeight);
            this.listWidth = listWidth;
            this.setX(listX);
            if (player != null) {
                for (ISlotKey key : keys) {
                    ItemStack stack = key.get(player);
                    if (!stack.isEmpty()) {
                        addEntry(new ItemEntry(stack, key));
                    }
                }
            }
        }

        @Override
        public int getRowWidth() {
            return listWidth - 12;
        }

        void selectByKey(@Nullable ISlotKey key) {
            if (key == null) {
                setSelected(null);
                return;
            }
            for (ItemEntry entry : children()) {
                if (entry.key.equals(key)) {
                    setSelected(entry);
                    return;
                }
            }
        }

        class ItemEntry extends ObjectSelectionList.Entry<ItemEntry> {
            private final ItemStack stack;
            private final ISlotKey key;

            ItemEntry(ItemStack stack, ISlotKey key) {
                this.stack = stack;
                this.key = key;
            }

            @Override
            public Component getNarration() {
                return stack.getHoverName();
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                boolean selected = ItemSelectionList.this.getSelected() == this;
                int textColor = (hovering || selected) ? 0xFFFFFFFF : 0xFFAAAAAA;
                graphics.fakeItem(stack, getContentX(), getContentY());
                graphics.text(font, stack.getHoverName(), getContentX() + 18, getContentYMiddle() - 4, textColor);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (event.button() == 0) {
                    ItemSelectionList.this.setSelected(this);
                    onItemSelected(stack, key);
                    return true;
                }
                return false;
            }
        }
    }
}
