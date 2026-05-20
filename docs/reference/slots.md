# Slot System

Slots represent positions in a player's inventory or container that the wheel can read from and write to. Quick provides three built-in slot types and an API to add your own.

## Built-in slot types

| Registry ID | Java class | Description |
|-------------|------------|-------------|
| `quick:inventory` | `InventorySlotKey(int index)` | A slot in the player's inventory by index |
| `quick:container` | `ContainerSlotKey(int index)` | A slot in the player's currently open container menu |
| `quick:empty` | `EmptySlotKey` | Always returns `ItemStack.EMPTY`; writes are rejected |

By default, Quick registers all inventory slots (`0` to `inventory.getContainerSize() - 1`) using `InventorySlotKey`.

## Extending slot access

To expose custom slots (for example, from a curios belt or a backpack mod), call `QuickApi.registerSlotAccess` from your mod initializer. The lambda is called every time Quick needs the slot list for a player.

```java
import com.ofekn.quick.api.common.QuickApi;

// In your mod initializer:
QuickApi.registerSlotAccess(player -> {
    MyInventory inv = MyMod.getInventory(player);
    if (inv == null) return Stream.empty();
    return IntStream.range(0, inv.size())
        .mapToObj(i -> new MySlotKey(i));
});
```

`registerSlotAccess` is thread-safe. The registered functions are called in the order they were registered.

You can also read the full resolved slot list for a player at any time:

```java
List<ISlotKey> slots = QuickApi.getSlots(player);
```

## The ISlotKey interface

`ISlotKey` represents a single slot position. Implement it to create a custom slot type:

```java
public interface ISlotKey {
    SlotType<?> type();                          // slot type for network serialization
    ItemStack get(Player player);                // read the item in this slot
    boolean set(Player player, ItemStack stack); // write to this slot; returns false if it fails
    boolean isModifiable(Player player);         // whether set() can succeed at all
}
```

`isModifiable` returning `true` does not guarantee `set` will succeed — it only indicates the slot is conceptually writable.

## Implementing a custom ISlotKey

```java
import com.ofekn.quick.api.common.ISlotKey;
import com.ofekn.quick.api.common.SlotType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MySlotKey(int index) implements ISlotKey {

    public static final StreamCodec<ByteBuf, MySlotKey> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, MySlotKey::index,
        MySlotKey::new
    );

    public static final SlotType<MySlotKey> TYPE = new SlotType<>(STREAM_CODEC);

    @Override
    public SlotType<?> type() { return TYPE; }

    @Override
    public ItemStack get(Player player) {
        return MyMod.getInventory(player).getItem(index);
    }

    @Override
    public boolean set(Player player, ItemStack stack) {
        MyInventory inv = MyMod.getInventory(player);
        if (inv == null || index >= inv.size()) return false;
        inv.setItem(index, stack);
        return true;
    }

    @Override
    public boolean isModifiable(Player player) {
        return MyMod.getInventory(player) != null;
    }
}
```

## Registering the SlotType

The slot type must be registered so the network codec can identify it. Register it on both client and server.

::: code-group

```java [Fabric]
Registry.register(
    QuickRegistry.SLOT_TYPE,
    Identifier.of("my_mod", "my_slot"),
    MySlotKey.TYPE
);
```

```java [NeoForge]
// In your mod constructor
DeferredRegister<SlotType<?>> SLOT_TYPES = DeferredRegister.create(QuickRegistryKeys.SLOT_TYPE, "my_mod");
SLOT_TYPES.register("my_slot", () -> MySlotKey.TYPE);
SLOT_TYPES.register(modEventBus);
```

:::
