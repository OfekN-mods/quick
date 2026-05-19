package com.ofekn.quick.api.common;

import net.minecraft.core.component.DataComponentType;

public final class QuickDataComponents {
    private QuickDataComponents() {}

    public static final DataComponentType<IItemAction> ITEM_ACTION = DataComponentType.<IItemAction>builder()
            .persistent(IItemAction.CODEC)
            .networkSynchronized(IItemAction.STREAM_CODEC)
            .build();
}
