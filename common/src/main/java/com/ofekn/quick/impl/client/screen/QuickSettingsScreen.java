package com.ofekn.quick.impl.client.screen;

import com.ofekn.quick.api.client.QuickClientRegistry;
import com.ofekn.quick.api.client.WheelLayout;
import com.ofekn.quick.impl.client.QuickKeyMappings;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class QuickSettingsScreen extends Screen {

    public QuickSettingsScreen() {
        super(Component.translatable("gui.quick.settings"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(getToggleLayoutButtonText(), QuickSettingsScreen::toggleLayout).pos(8, 8).build());
        var assignedActions = QuickKeyMappings.get().assignedActions;
        for (int i = 0; i < assignedActions.size(); i++) {
            String keybindName = assignedActions.get(i).getName();
            Component keybindComponent = Component.keybind(keybindName);
            Component buttonComponent = Component.translatable("gui.quick.settings.set_action", keybindComponent);
            final int finalI = i;
            addRenderableWidget(Button.builder(buttonComponent, _ -> editAction(finalI)).pos(8, 8 + 28 + 28 * i).build());
        }
    }

    private static void toggleLayout(Button button) {
        String layoutStr = QuickIntegrations.CONFIG.getWheelType();
        Identifier layoutId = Identifier.tryParse(layoutStr);
        WheelLayout layout = layoutId == null ? null : QuickClientRegistry.WHEEL_LAYOUT.getValue(layoutId);
        int index = layout == null ? -1 : QuickClientRegistry.WHEEL_LAYOUT.getId(layout);
        int newIndex = (index + 1) % QuickClientRegistry.WHEEL_LAYOUT.size();
        var opt = QuickClientRegistry.WHEEL_LAYOUT.get(newIndex);
        if (opt.isEmpty() || !opt.get().isBound()) {
            return;
        }
        WheelLayout newLayout = opt.get().value();
        Identifier newLayoutId = QuickClientRegistry.WHEEL_LAYOUT.getKey(newLayout);
        Objects.requireNonNull(newLayoutId);
        QuickIntegrations.CONFIG.setWheelType(newLayoutId.toString());
        button.setMessage(getToggleLayoutButtonText());
    }

    private static void editAction(int index) {
        Minecraft.getInstance().setScreen(new QuickEditActionScreen(index));
    }

    private static Component getToggleLayoutButtonText() {
        Component layout = Component.literal(QuickIntegrations.CONFIG.getWheelType()).withStyle(ChatFormatting.AQUA);
        return Component.translatable("gui.quick.settings.toggle_layout", layout);
    }
}
