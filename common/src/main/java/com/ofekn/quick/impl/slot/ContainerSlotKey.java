package com.ofekn.quick.impl.slot;

import com.ofekn.quick.api.ISlotKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ContainerSlotKey(int index) implements ISlotKey {
    public ContainerSlotKey {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive");
        }
    }

    @Override
    public ItemStack get(Player player) {
        var slots = player.containerMenu.slots;
        return index < slots.size() ? slots.get(index).getItem() : ItemStack.EMPTY;
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var slots = player.containerMenu.slots;
        if (index >= slots.size()) {
            return false;
        }
        var slot = slots.get(index);
        slot.set(stack);
        return slot.getItem() == stack; // check just in case
    }
}
