package com.ofekn.quick.neoforge.client;

import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.client.QuickKeyMappings;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = Quick.MID, value = Dist.CLIENT)
final class QuickClientEvents {
    private QuickClientEvents() {}

    @SubscribeEvent
    public static void event(ClientStartedEvent event) {

    }

    @SubscribeEvent
    public static void event(RegisterClientPayloadHandlersEvent event) {
    }

    @SubscribeEvent
    public static void event(RegisterKeyMappingsEvent event) {
        QuickKeyMappings.init(id -> {
            KeyMapping.Category category = new KeyMapping.Category(id);
            event.registerCategory(category);
            return category;
        }, event::register);
    }

    @SubscribeEvent
    public static void event(ClientTickEvent.Pre event) {
        QuickClient.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void event(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) return;
        // TODO support scancode event.getScanCode()
        if (QuickClient.onContainerScreenKeyPress(containerScreen, event.getKeyEvent())) {
            event.setCanceled(true);
        }
    }
}
