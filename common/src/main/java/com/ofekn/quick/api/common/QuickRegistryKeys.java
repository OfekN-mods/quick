package com.ofekn.quick.api.common;

import com.mojang.serialization.MapCodec;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class QuickRegistryKeys {
    private QuickRegistryKeys() {}

    public static final ResourceKey<Registry<SlotType<?>>> SLOT_TYPE = ResourceKey.createRegistryKey(Quick.id("slot_type"));
    public static final ResourceKey<Registry<MapCodec<? extends IItemAction>>> ITEM_ACTION = ResourceKey.createRegistryKey(Quick.id("item_action"));
}
