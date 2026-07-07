package com.ofekn.quick.impl.client.screen;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.impl.client.ActionBinding;
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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class QuickEditActionsScreen extends Screen {

    private static final int PADDING = 16;
    private static final int CONTENT_W = 320;
    private static final int BTN_H = 20;
    private static final int ITEM_H = 20;
    private static final int GAP = 4;
    private static final int TAB_BOTTOM_GAP = 2;

    private final ActionBinding[] pendingActions = new ActionBinding[10];
    private int selectedTab = 0;

    // Layout fields computed in init
    private int contentX;

    @Nullable private ItemStack selectedStack;
    @Nullable private ISlotKey selectedKey;
    @Nullable private String selectedMethod;

    private List<ISlotKey> options = List.of();
    @Nullable private Player player;

    private final Button[] tabButtons = new Button[10];
    private ItemSelectionList itemList;
    private Button byIdButton, byNameButton, bySlotButton;

    public QuickEditActionsScreen() {
        super(Component.translatable("gui.quick.edit_actions"));
        for (int i = 0; i < 10; i++) {
            pendingActions[i] = deserializeBinding(QuickIntegrations.CONFIG.getActionAssignment(i));
        }
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
        int methodWidth = (CONTENT_W - 2 * GAP) / 3;

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

        switch (pendingActions[tab]) {
            case ActionBinding.NoAction _ -> itemList.selectByKey(null);
            case ActionBinding.ByName(String name) -> {
                selectedMethod = "name";
                for (ISlotKey key : options) {
                    if (player == null) break;
                    ItemStack stack = key.get(player);
                    if (!stack.isEmpty() && stack.getHoverName().getString().equals(name)) {
                        selectedStack = stack;
                        selectedKey = key;
                        break;
                    }
                }
                itemList.selectByKey(selectedKey);
            }
            case ActionBinding.ById(Identifier id) -> {
                selectedMethod = "id";
                for (ISlotKey key : options) {
                    if (player == null) break;
                    ItemStack stack = key.get(player);
                    if (!stack.isEmpty() && stack.is(h -> h.is(id))) {
                        selectedStack = stack;
                        selectedKey = key;
                        break;
                    }
                }
                itemList.selectByKey(selectedKey);
            }
            case ActionBinding.BySlot(ISlotKey slotKey) -> {
                selectedMethod = "slot";
                selectedKey = slotKey;
                if (player != null) selectedStack = slotKey.get(player);
                itemList.selectByKey(selectedKey);
            }
        }
        refreshMethodButtons();
    }

    private void encodeCurrentTab() {
        if (selectedStack == null || selectedMethod == null) return;
        ActionBinding binding = switch (selectedMethod) {
            case "name" -> new ActionBinding.ByName(selectedStack.getHoverName().getString());
            case "id" -> selectedStack.typeHolder()
                    .unwrapKey()
                    .map(key -> (ActionBinding) new ActionBinding.ById(key.identifier()))
                    .orElse(ActionBinding.NoAction.INSTANCE);
            case "slot" -> new ActionBinding.BySlot(selectedKey);
            default -> throw new IllegalStateException("Unknown method: " + selectedMethod);
        };
        pendingActions[selectedTab] = binding;
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

    void onItemSelected(ItemStack stack, ISlotKey key) {
        selectedStack = stack;
        selectedKey = key;
        refreshMethodButtons();
    }

    private void saveAll() {
        encodeCurrentTab();
        for (int i = 0; i < 10; i++) {
            QuickIntegrations.CONFIG.setActionAssignment(i, serializeBinding(pendingActions[i]));
        }
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void cancel() {
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void clearTab() {
        pendingActions[selectedTab] = ActionBinding.NoAction.INSTANCE;
        selectedStack = null;
        selectedKey = null;
        selectedMethod = null;
        itemList.selectByKey(null);
        bySlotButton.active = false;
        refreshMethodButtons();
    }

    private static ActionBinding deserializeBinding(String s) {
        if (s == null || s.isEmpty()) return ActionBinding.NoAction.INSTANCE;
        try {
            return ActionBinding.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(s))
                    .result()
                    .orElse(ActionBinding.NoAction.INSTANCE);
        } catch (Exception e) {
            return ActionBinding.NoAction.INSTANCE;
        }
    }

    private static String serializeBinding(ActionBinding binding) {
        return ActionBinding.CODEC.encodeStart(JsonOps.INSTANCE, binding)
                .result()
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int titleY = PADDING + BTN_H + GAP + 2;
        graphics.centeredText(font, Component.translatable("gui.quick.edit_action", selectedTab), width / 2, titleY, 0xFFFFFF);

        LocalPlayer lp = player instanceof LocalPlayer p ? p : null;
        for (int i = 0; i < 10; i++) {
            var btn = tabButtons[i];
            int btnX = btn.getX();
            int btnY = btn.getY();
            int btnW = btn.getWidth();
            int textColor = (i == selectedTab) ? 0xFFFFFF55 : 0xFFFFFFFF;
            graphics.text(font, Component.literal("" + i), btnX + 6, btnY + 6, textColor);
            ItemStack stack = lp != null ? pendingActions[i].getIcon(lp) : new ItemStack(Items.BARRIER);
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
            addEntry(new ItemEntry(new ItemStack(Items.BARRIER), null));
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
            for (ItemEntry entry : children()) {
                if (key == null ? entry.key == null : key.equals(entry.key)) {
                    setSelected(entry);
                    return;
                }
            }
            setSelected(null);
        }

        class ItemEntry extends ObjectSelectionList.Entry<ItemEntry> {
            private final ItemStack stack;
            private final @Nullable ISlotKey key;

            ItemEntry(ItemStack stack, @Nullable ISlotKey key) {
                this.stack = stack;
                this.key = key;
            }

            private Component label() {
                return key == null ? Component.translatable("gui.quick.no_action") : stack.getHoverName();
            }

            @Override
            public Component getNarration() {
                return label();
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                boolean selected = ItemSelectionList.this.getSelected() == this;
                int textColor = (hovering || selected) ? 0xFFFFFFFF : 0xFFAAAAAA;
                graphics.fakeItem(stack, getContentX(), getContentY());
                graphics.text(font, label(), getContentX() + 18, getContentYMiddle() - 4, textColor);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (event.button() == 0) {
                    if (key == null) {
                        clearTab();
                    } else {
                        ItemSelectionList.this.setSelected(this);
                        onItemSelected(stack, key);
                    }
                    return true;
                }
                return false;
            }
        }
    }
}
