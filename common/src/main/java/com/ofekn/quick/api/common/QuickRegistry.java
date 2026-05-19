package com.ofekn.quick.api.common;

import com.mojang.serialization.MapCodec;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.core.Registry;

public final class QuickRegistry {
    private QuickRegistry() {}

    public static final Registry<SlotType<?>> SLOT_TYPE = QuickIntegrations.PLATFORM.makeRegistry(QuickRegistryKeys.SLOT_TYPE, true);
    public static final Registry<MapCodec<? extends IItemAction>> ITEM_ACTION = QuickIntegrations.PLATFORM.makeRegistry(QuickRegistryKeys.ITEM_ACTION, false);
}
