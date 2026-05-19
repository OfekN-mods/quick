package com.ofekn.quick.neoforge;

import com.ofekn.quick.api.Ref;
import com.ofekn.quick.integration.IInventoryExtender;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;

public enum CuriosIntegration implements IInventoryExtender {
    INSTANCE;

    @Override
    public void get(Player player, List<Ref<ItemStack>> result) {
        CuriosApi.getCuriosInventory(player).ifPresent(itemHandler -> {
            for (ICurioStacksHandler stackHandler : itemHandler.getCurios().values()) {
                var stacks = stackHandler.getStacks();
                Ref.forEveryIndex(
                        stacks::getStackInSlot,
                        stacks::setStackInSlot,
                        stacks.getSlots(),
                        result
                );
            }
        });
    }
}
