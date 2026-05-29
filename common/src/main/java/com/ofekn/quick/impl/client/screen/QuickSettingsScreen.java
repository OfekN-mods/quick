package com.ofekn.quick.impl.client.screen;

import com.ofekn.quick.api.client.QuickClientRegistry;
import com.ofekn.quick.api.client.WheelLayout;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class QuickSettingsScreen extends Screen {
    @Nullable
    private Button button;

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
        button = addRenderableWidget(Button.builder(Component.empty(), this::toggleLayout).pos(8, 8).build());
        updateToggleLayoutText();
    }

    private void toggleLayout(Button button) {
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
        updateToggleLayoutText();
    }

    private void updateToggleLayoutText() {
        if (button == null) {
            return;
        }
        Component layout = Component.literal(QuickIntegrations.CONFIG.getWheelType()).withStyle(ChatFormatting.AQUA);
        button.setMessage(Component.translatable("gui.quick.settings.toggle_layout", layout));
    }
}
