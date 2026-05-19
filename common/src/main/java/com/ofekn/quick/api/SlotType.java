package com.ofekn.quick.api;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SlotType<K extends ISlotKey>(StreamCodec<? super RegistryFriendlyByteBuf, K> streamCodec) {}
