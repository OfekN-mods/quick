package com.ofekn.quick.neoforge;

import com.ofekn.quick.impl.common.integration.IConfigIntegration;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

public enum NeoForgeConfigIntegration implements IConfigIntegration {
    INSTANCE;

    private final ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
    private final ModConfigSpec.BooleanValue storeItems = commonBuilder
            .comment("Whether to store items in the crafting stations when they are closed")
            .define("storeItems", true);
    private final ModConfigSpec commonSpec = commonBuilder.build();

    private final ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
    private final ModConfigSpec.ConfigValue<String> wheelType = clientBuilder
            .comment("The kind of wheel to use, currently there are only \"round\" and \"list\"")
            .define("wheelType", "round");
    @SuppressWarnings("unchecked")
    private final ModConfigSpec.ConfigValue<String>[] actionAssignments = java.util.stream.IntStream.range(0, 10)
            .mapToObj(i -> clientBuilder.define("action_" + i, ""))
            .<ModConfigSpec.ConfigValue<String>>toArray(ModConfigSpec.ConfigValue[]::new);
    private final ModConfigSpec clientSpec = clientBuilder.build();

    @ApiStatus.Internal
    static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, INSTANCE.commonSpec);
        modContainer.registerConfig(ModConfig.Type.CLIENT, INSTANCE.clientSpec);
    }

    @Override
    public String getWheelType() {
        return this.wheelType.get();
    }

    @Override
    public void setWheelType(String wheelType) {
        this.wheelType.set(wheelType);
        this.clientSpec.save();
    }

    @Override
    public boolean getStoreItems() {
        return this.storeItems.get();
    }

    @Override
    public String getActionAssignment(int index) {
        if (index < 0 || index >= actionAssignments.length) return "";
        String v = actionAssignments[index].get();
        return v != null ? v : "";
    }

    @Override
    public void setActionAssignment(int index, String assignment) {
        if (index < 0 || index >= actionAssignments.length) return;
        actionAssignments[index].set(assignment);
        clientSpec.save();
    }
}
