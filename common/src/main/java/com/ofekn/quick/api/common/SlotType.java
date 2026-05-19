package com.ofekn.quick.api.common;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SlotType<K extends ISlotKey>(
        StreamCodec<? super RegistryFriendlyByteBuf, K> streamCodec
) {

    public static final StreamCodec<RegistryFriendlyByteBuf, SlotType<?>> CODEC = ByteBufCodecs.registry(QuickRegistryKeys.SLOT_TYPE);
}
