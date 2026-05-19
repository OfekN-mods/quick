package com.ofekn.quick.api.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public interface IItemAction {
    Codec<IItemAction> CODEC = QuickRegistry.ITEM_ACTION.byNameCodec().dispatch(
            IItemAction::codec,
            Function.identity()
    );

    MapCodec<? extends IItemAction> codec();
    void onItemAction(Player player, ISlotKey slot);
}
