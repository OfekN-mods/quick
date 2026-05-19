package com.ofekn.quick.neoforge;

import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Quick.MID)
final class QuickEvents {
    private QuickEvents() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SBItemAction.TYPE, SBItemAction.CODEC, (packet, ctx) -> packet.handle(ctx.player()));
    }


}
