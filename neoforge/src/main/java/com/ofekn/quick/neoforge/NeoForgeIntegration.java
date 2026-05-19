package com.ofekn.quick.neoforge;

import com.mojang.logging.LogUtils;
import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.IConfigIntegration;
import com.ofekn.quick.impl.common.integration.IPlatformIntegration;
import com.ofekn.quick.impl.common.integration.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoForgeIntegration implements IPlatformIntegration {
    private static final Logger LOG = LogUtils.getLogger();
    @Nullable
    private static List<Consumer<IEventBus>> modBusInit = Collections.synchronizedList(new ArrayList<>());

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
    public <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key) {
        DeferredRegister<T> deferred = DeferredRegister.create(key, Quick.MID);
        addModBusInit(deferred::register);
        return deferred.makeRegistry(_ -> {});
    }

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry) {
        DeferredRegister<T> deferred = DeferredRegister.create(registry, Quick.MID);
        addModBusInit(deferred::register);
        // ide shows error for {@code deferred::register}, even though the compiler works
        return new Registrar<>() {
            @Override
            public <V extends T> Supplier<V> register(String name, Supplier<V> supplier) {
                return deferred.register(name, supplier);
            }
        };
    }

    private void addModBusInit(Consumer<IEventBus> task) {
        if (modBusInit == null) {
            LOG.error("Registration after mod bus initialized");
            return;
        }
        modBusInit.add(task);
    }


    @Override
    public void sendPacketToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public IConfigIntegration getConfigIntegration() {
        return NeoForgeConfigIntegration.INSTANCE;
    }

    public static void supplyModBus(IEventBus modBus) {
        if (modBusInit == null) {
            LOG.warn("Called SupplyModBus twice");
            return;
        }
        modBusInit.forEach(task -> task.accept(modBus));
        modBusInit = null;
    }
}