package com.ofekn.quick.api.common;

import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.datapack.QuickAction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class QuickRegistryKeys {
    private QuickRegistryKeys() {}

    public static final ResourceKey<Registry<SlotType<?>>> SLOT_TYPE = ResourceKey.createRegistryKey(Quick.id("slot_type"));
    public static final ResourceKey<Registry<ActionType<?>>> ACTION_TYPE = ResourceKey.createRegistryKey(Quick.id("action_type"));
    public static final ResourceKey<Registry<QuickAction>> ACTION = ResourceKey.createRegistryKey(Quick.id("action"));
}
