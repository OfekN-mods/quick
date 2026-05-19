package com.ofekn.quick.item;

import com.ofekn.quick.api.common.ISlotKey;
import net.minecraft.world.entity.player.Player;

@Deprecated
public interface IWheelItem {
    void onWheelAction(Player player, ISlotKey stackRef);
}
