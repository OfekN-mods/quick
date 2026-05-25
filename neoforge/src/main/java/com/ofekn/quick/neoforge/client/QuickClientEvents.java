package com.ofekn.quick.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.client.QuickKeyMappings;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

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
        QuickKeyMappings.get().containerInteract.setKeyConflictContext(KeyConflictContext.GUI);
    }

    @SubscribeEvent
    public static void event(ClientTickEvent.Pre event) {
        QuickClient.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void event(ScreenEvent.KeyPressed.Pre event) {
        containerInteract(event, InputConstants.getKey(event.getKeyEvent()));
    }

    @SubscribeEvent
    public static void event(ScreenEvent.MouseButtonPressed.Pre event) {
        containerInteract(event, InputConstants.Type.MOUSE.getOrCreate(event.getButton()));
    }

    private static <E extends ScreenEvent & ICancellableEvent> void containerInteract(E event, InputConstants.Key key) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) return;
        if (!QuickKeyMappings.get().containerInteract.isActiveAndMatches(key)) return;
        if (QuickClient.interactHoveredSlot(containerScreen)) {
            event.setCanceled(true);
        }
    }
}
