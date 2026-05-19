package com.ofekn.quick.api;

import com.ofekn.quick.impl.common.Quick;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class QuickRegistryKeys {
    public static final ResourceKey< Registry<SlotType<?>>> SLOT_TYPE = ResourceKey.createRegistryKey(Quick.id("slot_type"));
}
