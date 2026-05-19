package com.ofekn.quick.neoforge;

import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.IConfigIntegration;
import com.ofekn.quick.impl.common.integration.IPlatformIntegration;
import com.ofekn.quick.impl.common.integration.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NeoForgeIntegration implements IPlatformIntegration {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key, boolean sync) {
        DeferredRegister<T> deferred = DeferredRegister.create(key, Quick.MID);
        ModBusDistributor.add(deferred::register);
        return deferred.makeRegistry(builder -> builder.sync(sync));
    }

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry) {
        DeferredRegister<T> deferred = DeferredRegister.create(registry, Quick.MID);
        ModBusDistributor.add(deferred::register);
        // ide shows error for {@code deferred::register}, even though the compiler works
        return new Registrar<>() {
            @Override
            public <V extends T> Supplier<V> register(String name, Supplier<V> supplier) {
                return deferred.register(name, supplier);
            }
        };
    }

    @Override
    public void sendPacketToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public IConfigIntegration getConfigIntegration() {
        return NeoForgeConfigIntegration.INSTANCE;
    }
}