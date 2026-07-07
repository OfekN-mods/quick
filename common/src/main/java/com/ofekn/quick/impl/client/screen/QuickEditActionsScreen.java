package com.ofekn.quick.impl.client.screen;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.impl.client.ActionBinding;
import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
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

    private int contentX;
    private List<ISlotKey> options = List.of();
    @Nullable private Player player;

    private final Button[] tabButtons = new Button[10];
    private ItemSelectionList itemList;
    // Pre-allocated kind buttons (up to 4), rebuilt in buildEditPanel, shown/positioned in rebuildKindButtons
    private final Button[] kindButtons = new Button[4];
    private final @Nullable ActionBinding[] kindBindingSlots = new ActionBinding[4];

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
            new ItemSelectionList(minecraft, contentX, CONTENT_W, listH, listY, ITEM_H, options, player)
        );

        int methodY = height - PADDING - 2 * BTN_H - GAP;
        for (int i = 0; i < 4; i++) {
            final int fi = i;
            kindButtons[i] = addRenderableWidget(
                Button.builder(Component.empty(), _ -> {
                    var binding = kindBindingSlots[fi];
                    if (binding != null) {
                        pendingActions[selectedTab] = binding;
                        refreshKindButtonLabels();
                    }
                }).pos(0, methodY).size(1, BTN_H).build()
            );
            kindButtons[i].visible = false;
            kindBindingSlots[i] = null;
        }

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
        selectedTab = newTab;
        clearWidgets();
        buildTabButtons();
        buildEditPanel();
        loadTabState(selectedTab);
    }

    private void loadTabState(int tab) {
        ActionBinding current = pendingActions[tab];
        for (ItemSelectionList.ItemEntry entry : itemList.children()) {
            for (ActionBinding binding : entry.bindings.values()) {
                if (binding.equals(current)) {
                    itemList.setSelected(entry);
                    rebuildKindButtons(entry.bindings);
                    return;
                }
            }
        }
        itemList.setSelected(null);
        rebuildKindButtons(new EnumMap<>(ActionBinding.Type.class));
    }

    private void rebuildKindButtons(EnumMap<ActionBinding.Type, ActionBinding> bindings) {
        for (int i = 0; i < 4; i++) {
            kindButtons[i].visible = false;
            kindBindingSlots[i] = null;
        }

        var bindingEntries = new ArrayList<>(bindings.entrySet());
        int n = bindingEntries.size();
        int btnW = (CONTENT_W - Math.max(0, n - 1) * GAP) / n;
        ActionBinding current = pendingActions[selectedTab];

        for (int i = 0; i < n; i++) {
            ActionBinding.Type type = bindingEntries.get(i).getKey();
            ActionBinding binding = bindingEntries.get(i).getValue();
            kindBindingSlots[i] = binding;
            kindButtons[i].setMessage(
                binding.equals(current) ? type.label.copy().withStyle(ChatFormatting.GREEN) : type.label
            );
            kindButtons[i].setTooltip(binding instanceof ActionBinding.NoAction ? null : Tooltip.create(Component.literal(binding.toString())));
            kindButtons[i].setX(contentX + i * (btnW + GAP));
            kindButtons[i].setWidth(btnW);
            kindButtons[i].visible = true;
        }
    }

    private void refreshKindButtonLabels() {
        ItemSelectionList.ItemEntry entry = itemList.getSelected();
        if (entry == null) return;
        ActionBinding current = pendingActions[selectedTab];
        var bindingEntries = new ArrayList<>(entry.bindings.entrySet());
        for (int i = 0; i < bindingEntries.size() && kindButtons[i].visible; i++) {
            ActionBinding.Type type = bindingEntries.get(i).getKey();
            ActionBinding binding = bindingEntries.get(i).getValue();
            kindButtons[i].setMessage(
                binding.equals(current) ? type.label.copy().withStyle(ChatFormatting.GREEN) : type.label
            );
        }
    }

    void onEntrySelected(ItemSelectionList.ItemEntry entry) {
        ActionBinding.Type currentType = pendingActions[selectedTab].type();
        ActionBinding preferred = entry.bindings.get(currentType);
        pendingActions[selectedTab] = preferred != null ? preferred : entry.bindings.values().iterator().next();
        rebuildKindButtons(entry.bindings);
    }

    private void saveAll() {
        for (int i = 0; i < 10; i++) {
            QuickIntegrations.CONFIG.setActionAssignment(i, serializeBinding(pendingActions[i]));
        }
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void cancel() {
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private static ActionBinding deserializeBinding(String s) {
        if (s.isEmpty()) return ActionBinding.NoAction.INSTANCE;
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

            EnumMap<ActionBinding.Type, ActionBinding> noActionMap = new EnumMap<>(ActionBinding.Type.class);
            noActionMap.put(ActionBinding.Type.NO_ACTION, ActionBinding.NoAction.INSTANCE);
            addEntry(new ItemEntry(new ItemStack(Items.BARRIER), noActionMap));

            if (player == null) {
                return;
            }
            for (ISlotKey key : keys) {
                ItemStack stack = key.get(player);
                if (stack.isEmpty()) continue;
                EnumMap<ActionBinding.Type, ActionBinding> bindings = new EnumMap<>(ActionBinding.Type.class);
                bindings.put(ActionBinding.Type.BY_NAME, ActionBinding.ByName.of(stack));
                ActionBinding.ById.of(stack).ifPresent(b -> bindings.put(ActionBinding.Type.BY_ID, b));
                bindings.put(ActionBinding.Type.BY_SLOT, new ActionBinding.BySlot(key));
                addEntry(new ItemEntry(stack, bindings));
            }
        }

        @Override
        public int getRowWidth() {
            return listWidth - 12;
        }

        class ItemEntry extends ObjectSelectionList.Entry<ItemEntry> {
            final ItemStack displayStack;
            final EnumMap<ActionBinding.Type, ActionBinding> bindings;

            ItemEntry(ItemStack displayStack, EnumMap<ActionBinding.Type, ActionBinding> bindings) {
                this.displayStack = displayStack;
                this.bindings = bindings;
            }

            private Component label() {
                return bindings.containsKey(ActionBinding.Type.NO_ACTION)
                    ? Component.translatable("gui.quick.no_action")
                    : displayStack.getHoverName();
            }

            @Override
            public Component getNarration() {
                return label();
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                boolean selected = ItemSelectionList.this.getSelected() == this;
                int textColor = (hovering || selected) ? 0xFFFFFFFF : 0xFFAAAAAA;
                graphics.fakeItem(displayStack, getContentX(), getContentY());
                graphics.text(font, label(), getContentX() + 18, getContentYMiddle() - 4, textColor);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (event.button() == 0) {
                    ItemSelectionList.this.setSelected(this);
                    onEntrySelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}
