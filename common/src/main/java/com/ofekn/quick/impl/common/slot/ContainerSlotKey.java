package com.ofekn.quick.impl.common.slot;

import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ContainerSlotKey(int index) implements ISlotKey {
    public static final StreamCodec<ByteBuf, ContainerSlotKey> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ContainerSlotKey::index,
            ContainerSlotKey::new
    );
    public static final SlotType<ContainerSlotKey> TYPE = new SlotType<>(STREAM_CODEC);

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
        return getSlot(player).map(Slot::getItem).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var slotOpt = getSlot(player).filter(slot -> slot.allowModification(player));
        if (slotOpt.isEmpty()) {
            return false;
        }
        slotOpt.get().set(stack);
        return slotOpt.get().getItem() == stack; // check just in case
    }

    private Optional<Slot> getSlot(Player player) {
        var slots = player.containerMenu.slots;
        if (index > slots.size()) {
            return Optional.empty();
        }
        return Optional.of(slots.get(index));
    }

    @Override
    public boolean isModifiable(Player player) {
        return getSlot(player).map(slot -> slot.allowModification(player)).orElse(false);
    }
}
