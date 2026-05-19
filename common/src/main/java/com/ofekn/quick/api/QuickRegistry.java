package com.ofekn.quick.api;

import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.core.Registry;

public class QuickRegistry {
    public static final Registry<SlotType<?>> SLOT_TYPE = QuickIntegrations.PLATFORM.makeRegistry(QuickRegistryKeys.SLOT_TYPE);
}
