package com.ofekn.quick.impl.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.QuickApi;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public sealed interface ActionBinding {
    Codec<ActionBinding> CODEC = Codec.STRING.dispatch(
            "type",
            ab -> switch (ab) {
                case NoAction _ -> "no_action";
                case ByName _ -> "by_name";
                case ById _ -> "by_id";
                case BySlot _ -> "by_slot";
            },
            type -> (MapCodec<? extends ActionBinding>) switch (type) {
                case "no_action" -> MapCodec.unit(NoAction.INSTANCE);
                case "by_name" -> ByName.MAP_CODEC;
                case "by_id" -> ById.MAP_CODEC;
                case "by_slot" -> BySlot.MAP_CODEC;
                default -> throw new IllegalArgumentException("Unknown ActionBinding type: " + type);
            }
    );

    ItemStack getIcon(LocalPlayer player);
    void PerformAction(LocalPlayer player);

    enum NoAction implements ActionBinding {
        INSTANCE;

        @Override
        public ItemStack getIcon(LocalPlayer player) {
            return new ItemStack(Items.BARRIER);
        }

        @Override
        public void PerformAction(LocalPlayer player) {}
    }

    record ByName(String name) implements ActionBinding {
        static final MapCodec<ByName> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(Codec.STRING.fieldOf("name").forGetter(ByName::name))
                 .apply(i, ByName::new));

        public ByName of(ItemStack stack) {
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

        private Optional<ISlotKey> getSlot(Player player) {
            return QuickApi.getSlots(player)
                    .stream()
                    .filter(slotKey -> name.equals(getItemName(slotKey.get(player))))
                    .findFirst();
        }

        private static String getItemName(ItemStack stack) {
            return stack.getHoverName().getString();
        }
    }

    record ById(Identifier id) implements ActionBinding {
        static final MapCodec<ById> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(Identifier.CODEC.fieldOf("id").forGetter(ById::id))
                 .apply(i, ById::new));

        public Optional<ById> of(ItemStack stack) {
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

        private Optional<ISlotKey> getSlot(Player player) {
            return QuickApi.getSlots(player)
                    .stream()
                    .filter(slotKey -> slotKey.get(player).is(holder -> holder.is(id)))
                    .findFirst();
        }
    }

    record BySlot(ISlotKey slotKey) implements ActionBinding {
        static final MapCodec<BySlot> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
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
    }
}
