package com.ofekn.quick.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ofekn.quick.api.client.QuickClientRegistry;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.item.IWheelItem;
import com.ofekn.quick.api.common.QuickApi;
import com.ofekn.quick.api.client.IWheelOption;
import com.ofekn.quick.api.client.QuickClientApi;
import com.ofekn.quick.api.client.WheelData;
import com.ofekn.quick.api.common.QuickDataComponents;
import com.ofekn.quick.api.common.QuickRegistryKeys;
import com.ofekn.quick.impl.client.layout.ListWheelLayout;
import com.ofekn.quick.impl.client.layout.RoundWheelLayout;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.minecraft.core.Registry;
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
        List<ISlotKey> options = getOptions(player);
        if (options.isEmpty()) {
            return;
        }
        if (options.size() == 1 || !QuickKeyMappings.get().wheel.isDown()) {
            QuickIntegrations.PLATFORM.sendPacketToServer(new SBItemAction(options.getFirst()));
            return;
        }
        QuickClientApi.openWheel(new WheelData() {
            @Override
            public List<IWheelOption> options() {
                return options.stream()
                        .map(key -> (IWheelOption)new SlotWheelOption(minecraft, player, key, key.get(player)))
                        .toList();
            }

            @Override
            public Status status() {
                return isWheelButtonDown(minecraft) ? Status.SELECTING : Status.FINISH_SELECT;
            }
        });
    }

    private record SlotWheelOption(Minecraft minecraft, Player player, ISlotKey key, ItemStack stack) implements IWheelOption {
        @Override
        public void onSelect() {
            lastSelection = stack;
            QuickIntegrations.PLATFORM.sendPacketToServer(new SBItemAction(key));
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
    }

    private static boolean isWheelButtonDown(Minecraft minecraft) {
        // note: can't use keyMapping.isDown() because it's false when the menu is viewed
        var window = minecraft.getWindow();
        KeyMapping keyMapping = QuickKeyMappings.get().wheel;
        InputConstants.Key key = InputConstants.getKey(keyMapping.saveString()); // hack to get keyMapping.key
        return InputConstants.isKeyDown(window, key.getValue());
    }

    public static List<ISlotKey> getOptions(Player player) {
        Registry<com.ofekn.quick.impl.common.datapack.QuickAction> actionRegistry =
                player.level().registryAccess().lookupOrThrow(QuickRegistryKeys.ACTION);

        List<ISlotKey> allOptions = QuickApi.getSlots(player)
                .stream()
                .filter(key -> {
                    ItemStack stack = key.get(player);
                    if (stack.getItem() instanceof IWheelItem) return true;
                    if (stack.has(QuickDataComponents.ITEM_ACTION)) return true;
                    return actionRegistry.stream().anyMatch(qa -> qa.item().test(stack));
                })
                .toList();

        List<ISlotKey> result = new ArrayList<>();
        for (ISlotKey key : allOptions) {
            ItemStack stack = key.get(player);
            boolean isDuplicate = false;
            for (ISlotKey existing : result) {
                if (ItemStack.isSameItemSameComponents(existing.get(player), stack)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                result.add(key);
            }
        }

        for (int i = 0; i < result.size(); i++) {
            if (ItemStack.isSameItemSameComponents(result.get(i).get(player), lastSelection)) {
                ISlotKey key = result.remove(i);
                result.addFirst(key);
                break;
            }
        }
        return result;
    }
}
