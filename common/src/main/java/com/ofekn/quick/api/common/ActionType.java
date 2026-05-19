package com.ofekn.quick.api.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ActionType<A extends IItemAction>(
        MapCodec<A> codec,
        StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec
) {
    public static final Codec<ActionType<?>> CODEC = QuickRegistry.ACTION_TYPE.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ActionType<?>> STREAM_CODEC = ByteBufCodecs.registry(QuickRegistryKeys.ACTION_TYPE);
}
