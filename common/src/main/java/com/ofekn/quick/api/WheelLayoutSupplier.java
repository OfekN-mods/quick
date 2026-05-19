package com.ofekn.quick.api;

import com.ofekn.quick.impl.client.WheelPolygon;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntFunction;

// TODO revise API
@ApiStatus.Experimental
public interface WheelLayoutSupplier extends IntFunction<WheelPolygon[]>, StringRepresentable {

}
