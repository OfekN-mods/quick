package com.ofekn.quick.impl.common.slot;

import com.ofekn.quick.api.ISlotKey;
import com.ofekn.quick.api.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public enum EmptySlotKey implements ISlotKey {
    INSTANCE;

    public static final StreamCodec<ByteBuf, EmptySlotKey> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final SlotType<EmptySlotKey> TYPE = new SlotType<>("empty", STREAM_CODEC);

    @Override
    public SlotType<?> type() {
        return TYPE;
    }

    @Override
    public ItemStack get(Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isModifiable(Player player) {
        return false;
    }
}
