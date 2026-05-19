package com.ofekn.quick.neoforge;

import com.ofekn.quick.api.QuickApi;
import com.ofekn.quick.api.QuickRegistry;
import com.ofekn.quick.impl.slot.EmptySlotKey;
import com.ofekn.quick.integration.CoasIntegrations;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class CuriosIntegration {
    private CuriosIntegration() {}

    public static void register() {
        QuickApi.registerSlotAccess(CuriosIntegration::provide);

        var slotTypeRegistrar = CoasIntegrations.PLATFORM.createRegistrar(QuickRegistry.SLOT_TYPE);
        slotTypeRegistrar.register("curios", () -> EmptySlotKey.TYPE);
    }

    private static Stream<CuriosSlotKey> provide(Player player) {
        var inv = CuriosApi.getCuriosInventory(player);
        return inv.stream().map(ICuriosItemHandler::getCurios)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .flatMap(entry -> IntStream.range(0, entry.getValue().getSlots()).mapToObj(i -> new CuriosSlotKey(entry.getKey(), i)));
    }
}
