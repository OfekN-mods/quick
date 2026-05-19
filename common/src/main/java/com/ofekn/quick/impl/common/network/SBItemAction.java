package com.ofekn.quick.impl.common.network;

import com.ofekn.quick.api.common.IItemAction;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.item.IWheelItem;
import com.ofekn.quick.api.common.QuickDataComponents;
import com.ofekn.quick.api.common.QuickRegistryKeys;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record SBItemAction(ISlotKey key) implements CustomPacketPayload {
    public static final Type<SBItemAction> TYPE = new Type<>(Quick.id("sb_item_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SBItemAction> CODEC = StreamCodec.composite(
            ISlotKey.STREAM_CODEC, SBItemAction::key,
            SBItemAction::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(Player player) {
        ItemStack stack = key.get(player);
        if (stack.getItem() instanceof IWheelItem wheelItem) {
            wheelItem.onWheelAction(player, key);
            return;
        }
        IItemAction action = stack.get(QuickDataComponents.ITEM_ACTION);
        if (action != null) {
            action.onItemAction(player, key);
            return;
        }
        player.level().registryAccess().lookupOrThrow(QuickRegistryKeys.ACTION).stream()
                .filter(qa -> qa.item().test(stack))
                .findFirst()
                .ifPresent(qa -> qa.action().onItemAction(player, key));
    }
}
