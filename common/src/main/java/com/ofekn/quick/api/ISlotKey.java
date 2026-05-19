package com.ofekn.quick.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A unique representation of a slot position
 * Examples:
 *  - 10th slot in the inventory
 *  - 1st belt slot of curios
 *  - 2nd slot of the menu
 */
public interface ISlotKey {
    /**
     * @param player the container
     * @return the item in this slot
     */
    ItemStack get(Player player);

    /**
     * @param player the container
     * @param stack  the item stack to set
     *
     * @return true if the operation was successful
     */
    boolean set(Player player, ItemStack stack);
}
