package com.ofekn.quick.api;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class QuickApi {
    private QuickApi() {}
    private static final List<Function<Player, Stream<? extends ISlotKey>>> SLOT_ACCESS = new ArrayList<>();

    /**
     * @param slotAccess returns slots a player has
     */
    public static void registerSlotAccess(Function<Player, Stream<? extends ISlotKey>> slotAccess) {
        synchronized (SLOT_ACCESS) {
            SLOT_ACCESS.add(slotAccess);
        }
    }

    /**
     * @param player the player to check against
     * @return the list of slot he has
     */
    public static List<ISlotKey> getSlots(Player player) {
        synchronized (SLOT_ACCESS) {
            return SLOT_ACCESS.stream()
                    .<ISlotKey>flatMap(func -> func.apply(player))
                    .toList();
        }
    }
}
