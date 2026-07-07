package com.ofekn.quick.api.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SlotType<K extends ISlotKey>(
        MapCodec<K> codec,
        StreamCodec<? super RegistryFriendlyByteBuf, K> streamCodec
) {
    public static final Codec<SlotType<?>> CODEC = QuickRegistry.SLOT_TYPE.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotType<?>> STREAM_CODEC = ByteBufCodecs.registry(QuickRegistryKeys.SLOT_TYPE);
}
