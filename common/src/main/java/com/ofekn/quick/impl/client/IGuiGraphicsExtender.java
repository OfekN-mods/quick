package com.ofekn.quick.impl.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.joml.Vector2f;

public interface IGuiGraphicsExtender {
	void quick$renderColoredPolygon(RenderPipeline pipeline, Vector2f[] points, int color);
}
