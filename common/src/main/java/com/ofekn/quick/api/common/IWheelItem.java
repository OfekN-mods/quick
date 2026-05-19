package com.ofekn.quick.api.common;

import net.minecraft.world.entity.player.Player;

@Deprecated
public interface IWheelItem {
    void onWheelAction(Player player, ISlotKey stackRef);
}
