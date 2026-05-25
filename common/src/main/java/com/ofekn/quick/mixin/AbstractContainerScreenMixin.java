package com.ofekn.quick.mixin;

import com.ofekn.quick.impl.client.IAbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements IAbstractContainerScreen {
    @Shadow
    @Nullable
    public Slot hoveredSlot;

    @Override
    public @Nullable Slot quick$getHoveredSlot() {
        return hoveredSlot;
    }
}
