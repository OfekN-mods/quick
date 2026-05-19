package com.ofekn.quick.integration;

import com.ofekn.quick.api.Ref;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IInventoryExtender {
    void get(Player player, List<Ref<ItemStack>> result);
}
