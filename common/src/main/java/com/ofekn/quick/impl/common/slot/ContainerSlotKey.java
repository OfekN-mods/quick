package com.ofekn.quick.impl.common.slot;

import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ContainerSlotKey(int index) implements ISlotKey {
    public static final StreamCodec<ByteBuf, ContainerSlotKey> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ContainerSlotKey::index,
            ContainerSlotKey::new
    );
    public static final SlotType<ContainerSlotKey> TYPE = new SlotType<>("container", STREAM_CODEC);

    public ContainerSlotKey {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive");
        }
    }

    @Override
    public SlotType<?> type() {
        return TYPE;
    }

    @Override
    public ItemStack get(Player player) {
        var slots = player.containerMenu.slots;
        return index < slots.size() ? slots.get(index).getItem() : ItemStack.EMPTY;
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var slots = player.containerMenu.slots;
        if (index >= slots.size()) {
            return false;
        }
        var slot = slots.get(index);
        slot.set(stack);
        return slot.getItem() == stack; // check just in case
    }

    @Override
    public boolean isModifiable(Player player) {
        return true;
    }
}
