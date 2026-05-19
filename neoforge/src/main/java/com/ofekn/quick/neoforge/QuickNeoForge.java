package com.ofekn.quick.neoforge;


import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import com.ofekn.quick.item.CoasItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Quick.MID)
public class QuickNeoForge {
    public QuickNeoForge(IEventBus bus, ModContainer modContainer) {
        Quick.init();

        DeferredRegister.Items itemsReg = DeferredRegister.createItems(Quick.MID);
        for (CoasItem<?> item : CoasItem.getItems()) {
            registerItem(itemsReg, item);
        }
        itemsReg.register(bus);

        DeferredRegister<CreativeModeTab> tabsReg = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Quick.MID);
        tabsReg.register("tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.crafting_on_a_stick"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> CoasItem.CRAFTING_TABLE.get().getDefaultInstance())
                .displayItems((_, output) -> {
                    for (var item : CoasItem.getItems()) {
                        output.accept(item.get());
                    }
                }).build()
        );
        tabsReg.register(bus);

        if (QuickIntegrations.PLATFORM.isModLoaded("curios")) {
            CuriosIntegration.register();
        }

        NeoForgeConfigIntegration.register(modContainer);
        ModBusDistributor.supplyModBus(bus);
    }

    private <I extends Item> void registerItem(DeferredRegister.Items itemsReg, CoasItem<I> item) {
        item.bind(itemsReg.registerItem(item.getName(), item.getConstructor(), item.getProperties()));
    }
}