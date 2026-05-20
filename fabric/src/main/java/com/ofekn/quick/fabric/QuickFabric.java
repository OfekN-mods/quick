package com.ofekn.quick.fabric;

import com.ofekn.quick.api.common.QuickRegistryKeys;
import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.datapack.QuickAction;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class QuickFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Quick.init();
        registerPackets();
        registerDatapackRegistries();
        GsonConfigIntegration.load();
    }

    private void registerPackets() {
        PayloadTypeRegistry.serverboundPlay().register(SBItemAction.TYPE, SBItemAction.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SBItemAction.TYPE, (payload, context) -> payload.handle(context.player()));
    }

    private void registerDatapackRegistries() {
        DynamicRegistries.registerSynced(QuickRegistryKeys.ACTION, QuickAction.CODEC, QuickAction.CODEC);
    }
}
