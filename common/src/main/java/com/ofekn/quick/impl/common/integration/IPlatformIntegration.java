package com.ofekn.quick.impl.common.integration;

import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;

public interface IPlatformIntegration {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }


    /**
     * Registers a new registry
     *
     * @param key the id of the new registry
     * @return the new registry
     */
    <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key);


    /**
     * Creates a registrar
     *
     * @return A platform specific registrar
     */
    <T> Registrar<T> createRegistrar(Registry<T> registry);

    void sendPacketToServer(CustomPacketPayload payload);
    IConfigIntegration getConfigIntegration();
}