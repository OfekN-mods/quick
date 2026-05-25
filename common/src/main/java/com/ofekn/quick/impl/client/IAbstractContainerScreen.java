package com.ofekn.quick.impl.client;

import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public interface IAbstractContainerScreen {
    @Nullable Slot quick$getHoveredSlot();
}
