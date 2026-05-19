package com.ofekn.quick.fabric.client;

import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.client.QuickKeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class QuickFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuickKeyMappings.init(KeyMapping.Category::register, KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.START_CLIENT_TICK.register(QuickClient::tick);
    }
}
