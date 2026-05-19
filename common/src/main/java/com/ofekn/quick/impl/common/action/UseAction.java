package com.ofekn.quick.impl.common.action;

import com.mojang.serialization.MapCodec;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.IItemAction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public enum UseAction implements IItemAction {
    INSTANCE;

    public static final MapCodec<UseAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends IItemAction> codec() {
        return CODEC;
    }

    @Override
    public void onItemAction(Player player, ISlotKey slot) {
        ItemStack temp = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack stack = slot.get(player);
        if (!slot.set(player, ItemStack.EMPTY)) {
            return;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        InteractionResult result = stack.use(player.level(), player, InteractionHand.MAIN_HAND);
        ItemStack transform = null;
        if (result instanceof InteractionResult.Success success) {
            transform = success.heldItemTransformedTo();
        }
        if (transform == null) {
            transform = player.getItemInHand(InteractionHand.MAIN_HAND);
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        if (slot.set(player, transform)) {
            return;
        }
        // copied logic from the give command
        boolean added = player.getInventory().add(transform);
        if (added) {
            // not displaying fake item like the give command
            return;
        }
        ItemEntity drop = player.drop(transform, false);
        if (drop != null) {
            drop.setNoPickUpDelay();
            drop.setTarget(player.getUUID());
        }
    }
}
