package com.ofekn.quick.impl.common.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ofekn.quick.api.common.IItemAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record QuickAction(Ingredient item, IItemAction action) {
    public static final Codec<QuickAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("item").forGetter(QuickAction::item),
            IItemAction.CODEC.fieldOf("action").forGetter(QuickAction::action)
    ).apply(instance, QuickAction::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuickAction> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, QuickAction::item,
            IItemAction.STREAM_CODEC, QuickAction::action,
            QuickAction::new
    );
}
