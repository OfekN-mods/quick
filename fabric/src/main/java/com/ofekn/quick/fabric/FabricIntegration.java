package com.ofekn.quick.fabric;

import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.IConfigIntegration;
import com.ofekn.quick.impl.common.integration.IPlatformIntegration;
import com.ofekn.quick.impl.common.integration.Registrar;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

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
    public <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key, boolean sync) {
        var builder = FabricRegistryBuilder.create(key);
        if (sync) builder = builder.attribute(RegistryAttribute.SYNCED);
        return builder.buildAndRegister();
    }

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry) {
        return  new Registrar<>() {
            @Override
            public <V extends T> Supplier<V> register(String name, Supplier<V> supplier) {
                V value = Registry.register(registry, Quick.id(name), supplier.get());
                return () -> value;
            }
        };
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
