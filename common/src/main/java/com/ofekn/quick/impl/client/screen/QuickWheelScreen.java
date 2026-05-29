package com.ofekn.quick.impl.client.screen;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.ofekn.quick.api.client.*;
import com.ofekn.quick.impl.client.IGuiGraphicsExtender;
import com.ofekn.quick.impl.client.layout.RoundWheelLayout;
import com.ofekn.quick.impl.common.Quick;
import com.ofekn.quick.impl.common.integration.QuickIntegrations;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

public class QuickWheelScreen extends Screen {
	private static final WheelLayout FALLBACK_LAYOUT = RoundWheelLayout.INSTANCE;
	private static final Identifier SETTINGS_ICON_SPRITE = Quick.id("widget/settings");
	private static final Identifier SETTINGS_HOVER_ICON_SPRITE = Quick.id("widget/settings_hover");
	private static final int SETTINGS_BTN_OFFSET = 8;
	private static final int SETTINGS_BTN_SIZE = 16;
	private static final int SETTINGS_BTN_HOVER_SIZE = 32;
	private final WheelData data;
	@Nullable
	private IWheelOption selectionOption;
	private int selectionIndex;
	private int openTick;
	private boolean hoveringSettings;

	public QuickWheelScreen(WheelData data) {
		super(Component.empty());

		this.data = data;

		var initialOptions = data.options();
		this.selectionOption = initialOptions.isEmpty() ? null : initialOptions.getFirst();
		this.selectionIndex = 0;
		this.openTick = 0;
	}

	private boolean isOverSettingsButton(double mx, double my) {
		return 0 <= mx && mx < SETTINGS_BTN_HOVER_SIZE &&
				0 <= my && my < SETTINGS_BTN_HOVER_SIZE;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (event.button() == 0) {
			select();
			return true;
		}
		if (event.button() == 1) {
			onClose();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		openTick++;
		switch (data.status()) {
			case CANCEL -> onClose();
			case FINISH_SELECT -> select();
		}
	}

	private void select() {
		this.onClose();
		if (hoveringSettings) {
			this.minecraft.setScreen(new QuickSettingsScreen());
		} else if (selectionOption != null) {
			selectionOption.onSelect();
		}
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);

		hoveringSettings = isOverSettingsButton(mouseX, mouseY);
		if (hoveringSettings) {
			selectionOption = null;
			selectionIndex = -1;
			return;
		}

		float dmx = (float) mouseX - (float) width / 2;
		float dmy = (float) mouseY - (float) height / 2;

		var options = data.options();
		WheelPolygon[] layout = getLayout(options.size());
		float smallestDistance = Float.POSITIVE_INFINITY;
		int newSelection = 0;
		for (int i = 0; i < layout.length; i++) {
			for (Vector2f point : layout[i].points()) {
				float distance = point.distanceSquared(dmx, dmy);
				if (distance < smallestDistance) {
					smallestDistance = distance;
					newSelection = i;
				}
			}
		}
		selectionIndex = newSelection;
		selectionOption = newSelection < options.size() ? options.get(newSelection) : null;
	}

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		Matrix3x2fStack pos = graphics.pose();

		float t = Math.min(1.0f, getAnimationT(partialTick));

		var options = data.options();

		int numOptions = options.size();
		float centerX = width * 0.5f;
		float centerY = height * 0.5f;
		if (numOptions == 0) {
			int textColor = applyAlpha(0xFFFFFFFF, t);
            graphics.centeredText(font, Component.translatable("gui.crafting_on_a_stick.selection_wheel.no_tool"), (int)centerX, (int)centerY, textColor);
			renderSettingsButton(graphics, t);
			return;
		}
		float scale = easeScale(t);
		pos.pushMatrix();
		pos.translate(centerX, centerY);
		pos.scale(scale, scale);


		WheelPolygon[] layout = getLayout(numOptions);


		for (int i = 0; i < numOptions; i++) {
			WheelPolygon polygon = layout[i];
			int baseColor = selectionIndex == i ? 0xFFFFFFFF : 0x80FFFFFF;
			fill(polygon, graphics, RenderPipelines.GUI, 0, applyAlpha(baseColor, t));

			float x = polygon.center().x;
			float y = polygon.center().y;
			pos.pushMatrix();
			pos.translate(x, y);
			options.get(i).extract(graphics);
			pos.popMatrix();

			if (selectionIndex == i) {
				int overlayColor = applyAlpha(0x7FFFFFFF, t);
				fill(polygon, graphics, RenderPipelines.GUI, 10, overlayColor);
			}
		}
		pos.popMatrix();
		if (t > 0.99f) { // Only show tooltip when mostly faded in
			if (hoveringSettings) {
				graphics.setTooltipForNextFrame(Component.translatable("gui.quick.settings"), mouseX, mouseY);
			}
			if (selectionOption != null) {
				selectionOption.extractTooltip(graphics, mouseX, mouseY);
			}
		}

		renderSettingsButton(graphics, t);
	}

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		float t = getAnimationT(partialTick);
		graphics.fill(0, 0, width, height, applyAlpha(0x40000000, t));
        this.minecraft.gui.extractDeferredSubtitles();
	}

	private float easeScale(float t) {
		// alternative easing
//		return = (float) Math.pow(t, 4);
		float c1 = 1.70158f;
		float c3 = c1 + 1;
		return (float) (1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2));

	}

	private float getAnimationT(float partialTick) {
		// snap opens
//		return openTick > 4 ? 1 : 0;
		float t = (openTick + partialTick - 4) / 6;
		return Math.clamp(t, 0.0f, 1.0f);
	}

	private int applyAlpha(int color, float t) {
		int a = (int)((color >>> 24) * t) & 0xFF;
		return (a << 24) | (color & 0x00FFFFFF);
	}

	private WheelPolygon[] getLayout(int numOptions) {
		String layoutStr = QuickIntegrations.CONFIG.getWheelType();
		Identifier layoutId = Identifier.tryParse(layoutStr);
		WheelLayout layout = layoutId == null ? null : QuickClientRegistry.WHEEL_LAYOUT.getValue(layoutId);
		if (layout == null) {
			layout = FALLBACK_LAYOUT;
		}
		return layout.polygons(numOptions);
	}

	private void renderSettingsButton(GuiGraphicsExtractor graphics, float t) {
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				hoveringSettings ? SETTINGS_HOVER_ICON_SPRITE : SETTINGS_ICON_SPRITE,
				SETTINGS_BTN_OFFSET, SETTINGS_BTN_OFFSET,
				SETTINGS_BTN_SIZE, SETTINGS_BTN_SIZE,
				t
		);
	}

	private static void fill(WheelPolygon polygon, GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, float z, int color) {
		// TODO support z
		((IGuiGraphicsExtender)guiGraphics).quick$renderColoredPolygon(pipeline, polygon.points(), color);
	}
}
