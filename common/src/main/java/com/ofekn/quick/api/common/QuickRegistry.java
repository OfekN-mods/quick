package com.ofekn.quick.api.common;

import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.core.Registry;

public final class QuickRegistry {
    private QuickRegistry() {}

    public static final Registry<SlotType<?>> SLOT_TYPE = QuickIntegrations.PLATFORM.makeRegistry(QuickRegistryKeys.SLOT_TYPE);
}
