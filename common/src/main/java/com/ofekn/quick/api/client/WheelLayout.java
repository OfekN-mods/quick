package com.ofekn.quick.api.client;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface WheelLayout {
    WheelPolygon[] polygons(int numOptions);
}
