package com.ofekn.quick.neoforge.client;

import com.ofekn.quick.Quick;
import com.ofekn.quick.client.QuickClient;
import com.ofekn.quick.client.QuickKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = Quick.MID, value = Dist.CLIENT)
final class QuickClientEvents {
    private QuickClientEvents() {}

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
}
