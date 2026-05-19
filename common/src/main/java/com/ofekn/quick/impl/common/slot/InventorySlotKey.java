package com.ofekn.quick.impl.common.slot;

import com.ofekn.quick.api.ISlotKey;
import com.ofekn.quick.api.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record InventorySlotKey(int index) implements ISlotKey {
    public static final StreamCodec<ByteBuf, InventorySlotKey> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, InventorySlotKey::index,
            InventorySlotKey::new
    );
    public static final SlotType<InventorySlotKey> TYPE = new SlotType<>("inventory", STREAM_CODEC);

    public InventorySlotKey {
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
        return player.getInventory().getItem(index);
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var inventory = player.getInventory();
        if (index > inventory.getContainerSize()) {
            return false;
        }
        inventory.setItem(index, stack);
        return true;
    }

    @Override
    public boolean isModifiable(Player player) {
        return true;
    }
}
