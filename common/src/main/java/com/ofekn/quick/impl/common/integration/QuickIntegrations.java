package com.ofekn.quick.impl.common.integration;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.*;

public final class QuickIntegrations {
    private QuickIntegrations() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final IPlatformIntegration PLATFORM = loadPlatform();
    public static final IConfigIntegration CONFIG = PLATFORM.getConfigIntegration();

    private static IPlatformIntegration loadPlatform() {
        final IPlatformIntegration loadedService = ServiceLoader.load(IPlatformIntegration.class, QuickIntegrations.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load platform integration"));
        LOGGER.info("Loaded platform integration for {}", loadedService.getPlatformName());
        return loadedService;
    }
}