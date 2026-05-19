package com.ofekn.quick.api.client;

import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.core.Registry;

public final class QuickClientRegistry {
    private QuickClientRegistry() {}

    public static final Registry<WheelLayout> WHEEL_LAYOUT = QuickIntegrations.PLATFORM.makeRegistry(QuickClientRegistryKeys.WHEEL_LAYOUT, false);
}
