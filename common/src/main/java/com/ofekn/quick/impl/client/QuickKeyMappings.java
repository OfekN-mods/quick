package com.ofekn.quick.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ofekn.quick.impl.common.Quick;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public final class QuickKeyMappings {
	@Nullable
	private static QuickKeyMappings instance;
	private final KeyMapping.Category category;
	public final KeyMapping wheel;
	public final KeyMapping containerInteract;
	public final List<KeyMapping> assignedActions;

	public static void init(Function<Identifier, KeyMapping.Category> regCategory, Consumer<KeyMapping> regKey) {
		instance = new QuickKeyMappings(regCategory, regKey);
	}

	public static QuickKeyMappings get() {
		if (instance == null) {
			throw new IllegalStateException("called get before registry");
		}
		return instance;
	}

	private QuickKeyMappings(Function<Identifier, KeyMapping.Category> regCategory, Consumer<KeyMapping> regKey) {
		category = regCategory.apply(Quick.id("category"));
		wheel = key(regKey, "wheel", InputConstants.KEY_V);
		containerInteract = key(regKey, "container_interact", InputConstants.KEY_V);
		assignedActions = IntStream.range(0, 10).mapToObj(i -> key(regKey, "action_" + i, InputConstants.KEY_NUMPAD0 + i)).toList();
	}

	private KeyMapping key(Consumer<KeyMapping> regKey, String name, int defaultKey) {
		KeyMapping key = new KeyMapping(
				"key." + Quick.MID + "." + name,
				InputConstants.Type.KEYSYM, // All keys are on the keyboard
				defaultKey,
				category
		);
		regKey.accept(key);
		return key;
	}
}
