package com.ofekn.quick.api.common;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
    StreamCodec<RegistryFriendlyByteBuf, ISlotKey> STREAM_CODEC = SlotType.CODEC
            .dispatch(ISlotKey::type, SlotType::streamCodec);
    /**
     * @return the type of this slot
     */
    SlotType<?> type();

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

    /**
     * @param player the container
     * @return true if the slot is modifiable, else false
     * @apiNote if this method returns true it doesn't necessarily mean set will be successful
     */
    boolean isModifiable(Player player);
}
