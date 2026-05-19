package com.ofekn.quick.integration;

import java.util.function.Supplier;

public interface Registrar<T> {
	<V extends T> Supplier<V> register(String name, Supplier<V> supplier);
}
