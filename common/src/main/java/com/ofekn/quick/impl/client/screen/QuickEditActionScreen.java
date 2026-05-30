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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class QuickEditActionScreen extends Screen {

    private static final int PADDING = 8;
    private static final int BUTTON_W = 60;
    private static final int BUTTON_H = 20;
    private static final int ITEM_H = 20;
    private static final int LIST_H = 120;

    private final int actionIndex;

    @Nullable private ItemStack selectedStack;
    @Nullable private ISlotKey selectedKey;
    @Nullable private String selectedMethod;

    private ItemSelectionList itemList;
    private Button byIdButton;
    private Button byNameButton;
    private Button bySlotButton;
    private Button saveButton;

    public QuickEditActionScreen(int actionIndex) {
        super(Component.translatable("gui.quick.edit_action", actionIndex + 1));
        this.actionIndex = actionIndex;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        Player player = minecraft.player;
        List<ISlotKey> options = player != null ? QuickClient.getOptions(player) : List.of();

        int listTop = 20;
        itemList = new ItemSelectionList(minecraft, width, LIST_H, listTop, ITEM_H, options, player);
        addRenderableWidget(itemList);

        int methodY = listTop + LIST_H + PADDING;
        byIdButton   = addRenderableWidget(Button.builder(methodLabel("gui.quick.edit_action.by_id", "id"), b -> selectMethod("id"))
                .pos(PADDING, methodY).size(BUTTON_W, BUTTON_H).build());
        byNameButton = addRenderableWidget(Button.builder(methodLabel("gui.quick.edit_action.by_name", "name"), b -> selectMethod("name"))
                .pos(PADDING + BUTTON_W + 4, methodY).size(BUTTON_W, BUTTON_H).build());
        bySlotButton = addRenderableWidget(Button.builder(methodLabel("gui.quick.edit_action.by_slot", "slot"), b -> selectMethod("slot"))
                .pos(PADDING + (BUTTON_W + 4) * 2, methodY).size(BUTTON_W, BUTTON_H).build());
        bySlotButton.active = false;

        int actionY = methodY + BUTTON_H + PADDING;
        saveButton = addRenderableWidget(Button.builder(Component.translatable("gui.quick.edit_action.save"), b -> save())
                .pos(PADDING, actionY).size(BUTTON_W, BUTTON_H).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quick.cancel"), b -> cancel())
                .pos(PADDING + BUTTON_W + 4, actionY).size(BUTTON_W, BUTTON_H).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quick.edit_action.clear"), b -> clear())
                .pos(PADDING + (BUTTON_W + 4) * 2, actionY).size(BUTTON_W, BUTTON_H).build());

        updateSaveButton();
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        refreshMethodButtons();
        updateSaveButton();
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

    private void updateSaveButton() {
        saveButton.active = selectedStack != null && selectedMethod != null;
    }

    void onItemSelected(ItemStack stack, ISlotKey key) {
        selectedStack = stack;
        selectedKey = key;
        bySlotButton.active = key instanceof ContainerSlotKey;
        if ("slot".equals(selectedMethod) && !(key instanceof ContainerSlotKey)) {
            selectedMethod = null;
        }
        refreshMethodButtons();
        updateSaveButton();
    }

    private void save() {
        if (selectedStack == null || selectedMethod == null) return;
        String value = switch (selectedMethod) {
            case "id"   -> "id:" + BuiltInRegistries.ITEM.getKey(selectedStack.getItem());
            case "name" -> "name:" + selectedStack.getHoverName().getString();
            case "slot" -> "slot:" + ((ContainerSlotKey) selectedKey).index();
            default     -> throw new IllegalStateException("Unknown method: " + selectedMethod);
        };
        QuickIntegrations.CONFIG.setActionAssignment(actionIndex, value);
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void cancel() {
        minecraft.setScreen(new QuickSettingsScreen());
    }

    private void clear() {
        QuickIntegrations.CONFIG.setActionAssignment(actionIndex, "");
        minecraft.setScreen(new QuickSettingsScreen());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, 6, 0xFFFFFFFF);
    }

    // --- Item list ---

    class ItemSelectionList extends ObjectSelectionList<ItemSelectionList.ItemEntry> {

        ItemSelectionList(Minecraft mc, int width, int height, int top, int itemHeight,
                          List<ISlotKey> keys, @Nullable Player player) {
            super(mc, width, height, top, itemHeight);
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
            return width - 12;
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
                int textColor = (hovering || selected) ? 0xFFFFFF : 0xAAAAAA;
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
