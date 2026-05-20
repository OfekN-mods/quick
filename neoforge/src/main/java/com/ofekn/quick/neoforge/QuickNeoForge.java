package com.ofekn.quick.neoforge;


import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Quick.MID)
public class QuickNeoForge {
    public QuickNeoForge(IEventBus bus, ModContainer modContainer) {
        Quick.init();

        if (QuickIntegrations.PLATFORM.isModLoaded("curios")) {
            CuriosIntegration.register();
        }

        NeoForgeConfigIntegration.register(modContainer);
        ModBusDistributor.supplyModBus(bus);
    }
}