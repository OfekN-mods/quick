package com.ofekn.quick.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

public final class QuickClient {
    private QuickClient() {}

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
        QuickWheelScreen.trigger(minecraft, player);
    }
}
