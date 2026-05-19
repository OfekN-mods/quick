package com.ofekn.quick.api.common;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public interface IItemAction {
    Codec<IItemAction> CODEC = ActionType.CODEC.dispatch(
            IItemAction::type,
            ActionType::codec
    );
    StreamCodec<RegistryFriendlyByteBuf, IItemAction> STREAM_CODEC = ActionType.STREAM_CODEC.dispatch(
            IItemAction::type,
            ActionType::streamCodec
    );

    ActionType<?> type();
    void onItemAction(Player player, ISlotKey slot);
}
