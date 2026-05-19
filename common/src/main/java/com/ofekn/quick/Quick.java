package com.ofekn.quick;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

public final class Quick {
    private Quick() {}

    public static final String MID = "quick";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MID, path);
    }

    @ApiStatus.Internal
    public static void init() {}
}
