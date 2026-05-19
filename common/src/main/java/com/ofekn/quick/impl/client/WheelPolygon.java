package com.ofekn.quick.impl.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector2f;

public record WheelPolygon(Vector2f[] points, Vector2f center) {
	public void fill(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, float z, int color) {
        // TODO support z
		((IGuiGraphicsExtender)guiGraphics).quick$renderColoredPolygon(pipeline, points, color);
	}
}
