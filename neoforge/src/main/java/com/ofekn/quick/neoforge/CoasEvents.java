package com.ofekn.quick.neoforge;

import com.ofekn.quick.Quick;
import com.ofekn.quick.network.SBOpen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Quick.MID)
final class CoasEvents {
    private CoasEvents() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SBOpen.TYPE, SBOpen.CODEC, (packet, ctx) -> packet.handle(ctx.player()));
    }


}
