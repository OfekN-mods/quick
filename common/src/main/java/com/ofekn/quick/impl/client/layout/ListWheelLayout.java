package com.ofekn.quick.impl.client.layout;

import com.ofekn.quick.api.client.WheelLayout;
import com.ofekn.quick.api.client.WheelPolygon;
import org.joml.Vector2f;

public enum ListWheelLayout implements WheelLayout {
	INSTANCE;

	private static final float RADIUS = 9;
	private static final float SPACING = 4;

	@Override
	public WheelPolygon[] polygons(int numOptions) {
		if (numOptions == 0) {
			return new WheelPolygon[0];
		}

		int numAround = numOptions - 1;
		int numRight = numAround / 2;
		int numLeft = numAround - numRight;

		WheelPolygon[] result = new WheelPolygon[numOptions];
		for (int i = 0; i < numOptions; i++) {
			int order = i == 0 ? 0 : i < numLeft + 1 ? i - numLeft - 1 : i - numLeft;

			float x = order * (SPACING + RADIUS * 2);
			result[i] = new WheelPolygon(new Vector2f[] {
					new Vector2f(x - RADIUS, -RADIUS),
					new Vector2f(x - RADIUS, +RADIUS),
					new Vector2f(x + RADIUS, +RADIUS),
					new Vector2f(x + RADIUS, -RADIUS),
			}, new Vector2f(x, 0));
		}

		return result;
	}
}
