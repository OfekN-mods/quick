package com.ofekn.quick.impl.common;

import com.ofekn.quick.api.common.QuickApi;
import com.ofekn.quick.api.common.QuickRegistry;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.impl.common.slot.ContainerSlotKey;
import com.ofekn.quick.impl.common.slot.EmptySlotKey;
import com.ofekn.quick.impl.common.slot.InventorySlotKey;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.IntStream;

public final class Quick {
    private Quick() {}

    public static final String MID = "quick";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MID, path);
    }

    @ApiStatus.Internal
    public static void init() {
        // inventory slot keys
        QuickApi.registerSlotAccess(player -> {
            int inventorySize = player.getInventory().getContainerSize();
            return IntStream.range(0, inventorySize).mapToObj(InventorySlotKey::new);
        });

        var slotTypeRegistrar = QuickIntegrations.PLATFORM.createRegistrar(QuickRegistry.SLOT_TYPE);
        slotTypeRegistrar.register("empty", () -> EmptySlotKey.TYPE);
        slotTypeRegistrar.register("inventory", () -> InventorySlotKey.TYPE);
        slotTypeRegistrar.register("container", () -> ContainerSlotKey.TYPE);
    }
}
