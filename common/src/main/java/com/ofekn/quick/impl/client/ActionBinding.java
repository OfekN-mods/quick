package com.ofekn.quick.impl.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.QuickApi;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public sealed interface ActionBinding {
    Codec<ActionBinding> CODEC = Type.CODEC.dispatch(
            "type",
            ActionBinding::type,
            type -> type.codec
    );

    ItemStack getIcon(LocalPlayer player);
    void PerformAction(LocalPlayer player);
    Type type();

    enum Type implements StringRepresentable {
        NO_ACTION(NoAction.MAP_CODEC, Component.translatable("gui.quick.no_action")),
        BY_ID    (ById.MAP_CODEC    , Component.translatable("gui.quick.edit_action.by_id")),
        BY_NAME  (ByName.MAP_CODEC  , Component.translatable("gui.quick.edit_action.by_name")),
        BY_SLOT  (BySlot.MAP_CODEC  , Component.translatable("gui.quick.edit_action.by_slot"));

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        public final MapCodec<? extends ActionBinding> codec;
        public final Component label;

        Type(MapCodec<? extends ActionBinding> codec, Component label) {
            this.codec = codec;
            this.label = label;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    enum NoAction implements ActionBinding {
        INSTANCE;

        public static final MapCodec<NoAction> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public ItemStack getIcon(LocalPlayer player) {
            return new ItemStack(Items.BARRIER);
        }

        @Override
        public void PerformAction(LocalPlayer player) {}

        @Override
        public Type type() {
            return Type.NO_ACTION;
        }
    }

    record ByName(String name) implements ActionBinding {
        public static final MapCodec<ByName> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(Codec.STRING.fieldOf("name").forGetter(ByName::name))
                 .apply(i, ByName::new));

        public static ByName of(ItemStack stack) {
            return new ByName(getItemName(stack));
        }

        @Override
        public ItemStack getIcon(LocalPlayer player) {
            return getSlot(player).map(slotKey -> slotKey.get(player)).orElse(ItemStack.EMPTY);
        }

        @Override
        public void PerformAction(LocalPlayer player) {
            getSlot(player).ifPresent(slotKey -> QuickIntegrations.PLATFORM.sendPacketToServer(new SBItemAction(slotKey)));
        }

        @Override
        public Type type() {
            return Type.BY_NAME;
        }

        private Optional<ISlotKey> getSlot(Player player) {
            return QuickApi.getSlots(player)
                    .stream()
                    .filter(slotKey -> name.equals(getItemName(slotKey.get(player))))
                    .findFirst();
        }

        private static String getItemName(ItemStack stack) {
            return stack.getHoverName().getString();
        }

        @Override public String toString() { return name; }
    }

    record ById(Identifier id) implements ActionBinding {
        public static final MapCodec<ById> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(Identifier.CODEC.fieldOf("id").forGetter(ById::id))
                 .apply(i, ById::new));

        public static Optional<ById> of(ItemStack stack) {
            // should never be Optional.empty, but returning just in case
            return stack.typeHolder()
                    .unwrapKey()
                    .map(key -> new ById(key.identifier()));
        }

        @Override
        public ItemStack getIcon(LocalPlayer player) {
            return getSlot(player).map(slotKey -> slotKey.get(player)).orElse(ItemStack.EMPTY);
        }

        @Override
        public void PerformAction(LocalPlayer player) {
            getSlot(player).ifPresent(slotKey -> QuickIntegrations.PLATFORM.sendPacketToServer(new SBItemAction(slotKey)));
        }

        @Override
        public Type type() {
            return Type.BY_ID;
        }

        private Optional<ISlotKey> getSlot(Player player) {
            return QuickApi.getSlots(player)
                    .stream()
                    .filter(slotKey -> slotKey.get(player).is(holder -> holder.is(id)))
                    .findFirst();
        }

        @Override public String toString() { return id.toString(); }
    }

    record BySlot(ISlotKey slotKey) implements ActionBinding {
        public static final MapCodec<BySlot> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(ISlotKey.CODEC.fieldOf("slot").forGetter(BySlot::slotKey))
                 .apply(i, BySlot::new));

        @Override
        public ItemStack getIcon(LocalPlayer player) {
            return slotKey.get(player);
        }

        @Override
        public void PerformAction(LocalPlayer player) {
            QuickIntegrations.PLATFORM.sendPacketToServer(new SBItemAction(slotKey));
        }

        @Override
        public Type type() {
            return Type.BY_SLOT;
        }

        @Override public String toString() { return slotKey.toString(); }
    }
}
