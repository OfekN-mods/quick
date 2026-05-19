package com.ofekn.quick.neoforge;

import com.ofekn.quick.api.ISlotKey;
import com.ofekn.quick.api.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public record CuriosSlotKey(String identifier, int index) implements ISlotKey {
    public static final StreamCodec<ByteBuf, CuriosSlotKey> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CuriosSlotKey::identifier,
            ByteBufCodecs.VAR_INT, CuriosSlotKey::index,
            CuriosSlotKey::new
    );
    public static final SlotType<CuriosSlotKey> TYPE = new SlotType<>(STREAM_CODEC);

    @Override
    public SlotType<?> type() {
        return TYPE;
    }

    @Override
    public ItemStack get(Player player) {
        return getStackHandler(player)
                .map(handler -> handler.getStackInSlot(index))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        var handlerOpt = getStackHandler(player);
        if (handlerOpt.isEmpty()) {
            return false;
        }
        var handler = handlerOpt.get();
        handler.setStackInSlot(index, stack);
        return handler.getStackInSlot(index) == stack; // check just in case
    }

    @Override
    public boolean isModifiable(Player player) {
        return true;
    }

    private Optional<IDynamicStackHandler> getStackHandler(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.getStacksHandler(identifier))
                .map(ICurioStacksHandler::getStacks)
                .filter(handler -> index < handler.getSlots());
    }
}
