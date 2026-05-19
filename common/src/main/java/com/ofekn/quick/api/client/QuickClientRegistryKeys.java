package com.ofekn.quick.api.client;

import com.ofekn.quick.api.common.SlotType;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class QuickClientRegistryKeys {
    private QuickClientRegistryKeys() {}

    public static final ResourceKey<Registry<WheelLayout>> WHEEL_LAYOUT = ResourceKey.createRegistryKey(Quick.id("wheel_layout"));
}
