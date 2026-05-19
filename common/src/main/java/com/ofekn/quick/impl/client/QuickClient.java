package com.ofekn.quick.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ofekn.quick.api.client.QuickClientRegistry;
import com.ofekn.quick.api.common.IWheelItem;
import com.ofekn.quick.api.common.QuickApi;
import com.ofekn.quick.api.client.IWheelOption;
import com.ofekn.quick.api.client.QuickClientApi;
import com.ofekn.quick.api.client.WheelData;
import com.ofekn.quick.api.common.QuickRegistry;
import com.ofekn.quick.impl.client.layout.ListWheelLayout;
import com.ofekn.quick.impl.client.layout.RoundWheelLayout;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.network.SBOpen;
import com.ofekn.quick.impl.common.slot.ContainerSlotKey;
import com.ofekn.quick.impl.common.slot.EmptySlotKey;
import com.ofekn.quick.impl.common.slot.InventorySlotKey;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public final class QuickClient {
    private QuickClient() {}

    public static void init() {
        var layoutRegistrar = QuickIntegrations.PLATFORM.createRegistrar(QuickClientRegistry.WHEEL_LAYOUT);
        layoutRegistrar.register("round", () -> ListWheelLayout.INSTANCE);
//        layoutRegistrar.register("polygonal", () -> PolygonalWheelLayout.INSTANCE);
        layoutRegistrar.register("list", () -> RoundWheelLayout.INSTANCE);

    }

    @ApiStatus.Internal
    public static void tick(Minecraft minecraft) {
        openWheelIfClicked(minecraft);
    }

    private static void openWheelIfClicked(Minecraft minecraft) {
        if (!QuickKeyMappings.get().wheel.consumeClick()) {
            return;
        }
        if (minecraft.screen != null) {
            return;
        }
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        trigger(minecraft, player);
    }

    // TODO remove the code below (old code)

    private static ItemStack lastSelection = ItemStack.EMPTY;
    public static void trigger(Minecraft minecraft, Player player) {
        List<ItemStack> options = getOptions(player);
        if (options.isEmpty()) {
            return;
        }
        ItemStack firstOption = options.getFirst();
        if (options.size() == 1 || !QuickKeyMappings.get().wheel.isDown()) {
            QuickIntegrations.PLATFORM.sendPacketToServer(new SBOpen(firstOption));
            return;
        }
        QuickClientApi.openWheel(new WheelData() {
            @Override
            public List<IWheelOption> options() {
                return options.stream().<IWheelOption>map(stack -> new IWheelOption() {
                    @Override
                    public void onSelect() {
                        lastSelection = stack;
                        QuickIntegrations.PLATFORM.sendPacketToServer(new SBOpen(stack));
                    }

                    @Override
                    public void extract(GuiGraphicsExtractor graphics) {
                        graphics.fakeItem(stack, -8, -8);
                    }

                    @Override
                    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
                        graphics.setTooltipForNextFrame(
                                minecraft.font,
                                Screen.getTooltipFromItem(minecraft, stack),
                                stack.getTooltipImage(),
                                mouseX,
                                mouseY,
                                stack.get(DataComponents.TOOLTIP_STYLE)
                        );
                    }
                }).toList();
            }

            @Override
            public Status status() {
                return isWheelButtonDown(minecraft) ? Status.SELECTING : Status.FINISH_SELECT;
            }
        });
    }

    private static boolean isWheelButtonDown(Minecraft minecraft) {
        // note: can't use keyMapping.isDown() because it's false when the menu is viewed
        var window = minecraft.getWindow();
        KeyMapping keyMapping = QuickKeyMappings.get().wheel;
        InputConstants.Key key = InputConstants.getKey(keyMapping.saveString()); // hack to get keyMapping.key
        return InputConstants.isKeyDown(window, key.getValue());
    }

    public static List<ItemStack> getOptions(Player player) {
        List<ItemStack> allOptions = QuickApi.getSlots(player)
                .stream()
                .map(key -> key.get(player))
                .map(stack -> stack.getItem() instanceof IWheelItem item ? item.getWheelRepresentative(player, stack) : ItemStack.EMPTY)
                .filter(stack -> !stack.isEmpty())
                .toList();

        // Deduplicate using ItemStack.isSameItemSameComponents
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : allOptions) {
            boolean isDuplicate = false;
            for (ItemStack existing : result) {
                if (ItemStack.isSameItemSameComponents(stack, existing)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                result.add(stack);
            }
        }

        for (int i = 0; i < result.size(); i++) {
            if (ItemStack.isSameItemSameComponents(result.get(i), lastSelection)) {
                result.remove(i);
                result.addFirst(lastSelection);
                break;
            }
        }
        return result;
    }
}
