package com.ofekn.quick;

import com.ofekn.quick.api.QuickApi;
import com.ofekn.quick.impl.slot.InventorySlotKey;
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
    }
}
