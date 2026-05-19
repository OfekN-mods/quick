package com.ofekn.quick.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.joml.Vector2f;

public interface IGuiGraphicsExtender {
	void coas$renderColoredPolygon(RenderPipeline pipeline, Vector2f[] points, int color);
}
