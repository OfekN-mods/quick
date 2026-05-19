package com.ofekn.quick.api.client;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2f;

@ApiStatus.Experimental
public record WheelPolygon(Vector2f[] points, Vector2f center) {}
