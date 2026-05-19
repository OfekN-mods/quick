package com.ofekn.quick.api.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Represents an option in a wheel screen
 */
public interface IWheelOption {
    /**
     * Called when the option is selected
     */
    void onSelect();

    /**
     * Should extract the geometry centered at (0,0)
     * <p>
     * For example {@code graphics.item(stack, -8, -8);}
     *
     * @param graphics access to extract geometry
     */
    void extract(GuiGraphicsExtractor graphics);

    /**
     * Should extract the geometry at (mouseX, mouseY)
     * <p>
     * For example {@code graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);}
     *
     * @param graphics access to extract geometry
     * @param mouseX   horizontal mouse position
     * @param mouseY   vertical mouse position
     */
    void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY);
}
