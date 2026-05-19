package com.ofekn.quick.impl.slot;

import com.ofekn.quick.api.ISlotKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public enum EmptySlotKey implements ISlotKey {
    INSTANCE;

    @Override
    public ItemStack get(Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        return false;
    }
}
