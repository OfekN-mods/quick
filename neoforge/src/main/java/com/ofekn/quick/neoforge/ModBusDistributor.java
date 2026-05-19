package com.ofekn.quick.neoforge;

import com.mojang.datafixers.util.Either;
import net.neoforged.bus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ModBusDistributor {
    private ModBusDistributor() {}
    private static Either<List<Consumer<IEventBus>>, IEventBus> state = Either.left(new ArrayList<>());

    public static synchronized void add(Consumer<IEventBus> task) {
        state.ifLeft(list -> list.add(task));
        state.ifRight(task);
    }

    public static synchronized void supplyModBus(IEventBus bus) {
        state.ifRight(_ -> {
            throw new IllegalStateException("already supplied a bus");
        });
        state.orThrow().forEach(task -> task.accept(bus));
        state = Either.right(bus);
    }
}
