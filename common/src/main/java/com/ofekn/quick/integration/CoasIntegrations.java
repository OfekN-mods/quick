package com.ofekn.quick.integration;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Supplier;

public final class CoasIntegrations {
    private CoasIntegrations() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final IPlatformIntegration PLATFORM = loadPlatform();
    public static final IConfigIntegration CONFIG = PLATFORM.getConfigIntegration();

    private static IPlatformIntegration loadPlatform() {
        final IPlatformIntegration loadedService = ServiceLoader.load(IPlatformIntegration.class, CoasIntegrations.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load platform integration"));
        LOGGER.info("Loaded platform integration for {}", loadedService.getPlatformName());
        return loadedService;
    }
}