package com.ofekn.quick.network;

import com.ofekn.quick.Quick;
import com.ofekn.quick.QuickUtils;
import com.ofekn.quick.api.ISlotKey;
import com.ofekn.quick.api.IWheelItem;
import com.ofekn.quick.api.QuickApi;
import com.ofekn.quick.api.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SBOpen(ItemStack selected) implements CustomPacketPayload {
	public static final Type<SBOpen> TYPE = new Type<>(Quick.id("sb_open"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SBOpen> CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC, SBOpen::selected,
			SBOpen::new
	);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

	public void handle(Player player) {
        List<ISlotKey> inventory = QuickApi.getSlots(player);
        for (ISlotKey key : inventory) {
            ItemStack inventoryStack = key.get(player);
            if (inventoryStack.isEmpty()) {
                continue;
            }
            if (!(inventoryStack.getItem() instanceof IWheelItem wheelItem)) {
                continue;
            }
            ItemStack representative = wheelItem.getWheelRepresentative(player, inventoryStack);
            if (!ItemStack.isSameItemSameComponents(representative, selected)) {
                continue;
            }
            wheelItem.onWheelAction(player, key);
            break;
        }
	}
}
