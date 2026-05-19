package com.ofekn.quick.fabric;

import com.ofekn.quick.integration.IConfigIntegration;
import com.ofekn.quick.integration.IPlatformIntegration;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricIntegration implements IPlatformIntegration {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void sendPacketToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public IConfigIntegration getConfigIntegration() {
        return GsonConfigIntegration.INSTANCE;
    }
}
