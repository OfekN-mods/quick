package com.ofekn.quick.impl.slot;

import com.ofekn.quick.api.ISlotKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record InventorySlotKey(int index) implements ISlotKey {

    public InventorySlotKey {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive");
        }
    }

    @Override
    public ItemStack get(Player player) {
        return player.getInventory().getItem(index);
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var inventory = player.getInventory();
        if (index > inventory.getContainerSize()) {
            return false;
        }
        inventory.setItem(index, stack);
        return true;
    }

    @Override
    public boolean isModifiable(Player player) {
        return true;
    }
}
