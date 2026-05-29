package com.ofekn.quick.api.client;

import com.ofekn.quick.impl.client.screen.QuickWheelScreen;
import net.minecraft.client.Minecraft;

public final class QuickClientApi {
    private QuickClientApi() {}

    public static void openWheel(WheelData data) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) {
            return;
        }
        mc.setScreen(new QuickWheelScreen(data));
    }
}
