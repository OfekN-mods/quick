package com.ofekn.quick.neoforge.client;

import com.ofekn.quick.Quick;
import com.ofekn.quick.client.QuickClient;
import com.ofekn.quick.client.CoasKeyMappings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = Quick.MID, value = Dist.CLIENT)
final class CoasClientEvents {
    private CoasClientEvents() {}

    @SubscribeEvent
    public static void event(RegisterClientPayloadHandlersEvent event) {
    }

    @SubscribeEvent
    public static void event(RegisterKeyMappingsEvent event) {
        CoasKeyMappings.LIST.forEach(event::register);
    }

    @SubscribeEvent
    public static void event(ClientTickEvent.Pre event) {
        QuickClient.tick(Minecraft.getInstance());
    }
}
