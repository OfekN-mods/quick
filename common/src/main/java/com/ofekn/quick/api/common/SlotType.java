package com.ofekn.quick.api.common;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SlotType<K extends ISlotKey>(String name, StreamCodec<? super RegistryFriendlyByteBuf, K> streamCodec) {}
