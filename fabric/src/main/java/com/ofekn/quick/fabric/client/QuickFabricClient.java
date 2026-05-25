package com.ofekn.quick.fabric.client;

import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.client.QuickKeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class QuickFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuickClient.init();
        QuickKeyMappings.init(KeyMapping.Category::register, KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.START_CLIENT_TICK.register(QuickClient::tick);
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;
            ScreenKeyboardEvents.allowKeyPress(screen).register((s, keyEvent) ->
                    !QuickClient.onContainerScreenKeyPress(containerScreen, keyEvent));
        });
    }
}
