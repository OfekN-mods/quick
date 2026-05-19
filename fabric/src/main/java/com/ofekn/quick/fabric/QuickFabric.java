package com.ofekn.quick.fabric;

import com.ofekn.quick.api.common.QuickRegistryKeys;
import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.datapack.QuickAction;
import com.ofekn.quick.item.CoasItem;
import com.ofekn.quick.impl.common.network.SBItemAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class QuickFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Quick.init();
        registerItems();
        registerPackets();
        registerDatapackRegistries();
        GsonConfigIntegration.load();
    }

    private void registerItems() {
        for (CoasItem<?> item : CoasItem.getItems()) {
            registerItem(item);
        }

        CreativeModeTab tab = FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.crafting_on_a_stick"))
                .icon(() -> CoasItem.CRAFTING_TABLE.get().getDefaultInstance())
                .displayItems((_, output) -> {
                    for (var item : CoasItem.getItems()) {
                        output.accept(item.get());
                    }
                }).build();

        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB, Quick.id("creative_tab")
        );
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);
    }

    private <I extends Item> void registerItem(CoasItem<I> item) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Quick.id(item.getName()));
        I result = item.getConstructor().apply(item.getProperties().apply(new Item.Properties()).setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, result);
        item.bind(() -> result);
    }

    private void registerPackets() {
        PayloadTypeRegistry.serverboundPlay().register(SBItemAction.TYPE, SBItemAction.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SBItemAction.TYPE, (payload, context) -> payload.handle(context.player()));
    }

    private void registerDatapackRegistries() {
        DynamicRegistries.registerSynced(QuickRegistryKeys.ACTION, QuickAction.CODEC, QuickAction.CODEC);
    }
}
