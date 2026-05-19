package com.ofekn.quick;

import com.ofekn.quick.api.ISlotKey;
import com.ofekn.quick.api.QuickApi;
import com.ofekn.quick.api.Ref;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// TODO delete
public final class QuickUtils {
    private QuickUtils() {}

    public static Optional<Ref<ItemStack>> searchInventory(Player player, Item item) {
        return searchInventory(player, item, _ -> true);
    }

    public static Optional<Ref<ItemStack>> searchInventory(Player player, Item item, Predicate<ItemStack> filter) {
        List<Ref<ItemStack>> inventory = getFullInventory(player);
        for (Ref<ItemStack> ref : inventory) {
            ItemStack invStack = ref.get();
            if (!invStack.isEmpty() && invStack.getItem() == item && filter.test(invStack)) {
                return Optional.of(ref);
            }
        }
        return Optional.empty();
    }

    public static List<Ref<ItemStack>> getFullInventory(Player player) {
        return QuickApi.getSlots(player).stream().<Ref<ItemStack>>map(slot -> new MigrationAssignedSlot(player, slot)).toList();
    }

    private record MigrationAssignedSlot(Player player, ISlotKey key) implements Ref<ItemStack> {

        @Override
        public ItemStack get() {
            return key.get(player);
        }

        @Override
        public void set(ItemStack value) {
            key.set(player, value);
        }
    }
}
