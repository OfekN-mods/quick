package com.ofekn.quick.api;

import com.ofekn.quick.integration.CoasIntegrations;
import net.minecraft.core.Registry;

public class QuickRegistry {
    public static final Registry<SlotType<?>> SLOT_TYPE = CoasIntegrations.PLATFORM.makeRegistry(QuickRegistryKeys.SLOT_TYPE);
}
