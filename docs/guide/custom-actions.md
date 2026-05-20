# Custom Actions

You can add your own action types by implementing `IItemAction`, defining an `ActionType`, and registering it to `QuickRegistry.ACTION_TYPE`.

## Implementing IItemAction

Use a `record` when the action carries configuration, or a singleton `enum` when it has no fields:

```java
// With data — each instance can have different values
public record PowerAction(int power) implements IItemAction {
    public static final MapCodec<PowerAction> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("power").forGetter(PowerAction::power)
        ).apply(instance, PowerAction::new)
    );
    public static final StreamCodec<ByteBuf, PowerAction> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, PowerAction::power,
        PowerAction::new
    );
    public static final ActionType<PowerAction> TYPE = new ActionType<>(CODEC, STREAM_CODEC);

    @Override
    public ActionType<?> type() { return TYPE; }

    @Override
    public void onItemAction(Player player, ISlotKey slot) {
        ItemStack stack = slot.get(player);
        // use this.power() in your logic
    }
}

// Singleton — no configurable fields, same pattern as quick:use
public enum ResetAction implements IItemAction {
    INSTANCE;

    public static final MapCodec<ResetAction> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, ResetAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final ActionType<ResetAction> TYPE = new ActionType<>(CODEC, STREAM_CODEC);

    @Override
    public ActionType<?> type() { return TYPE; }

    @Override
    public void onItemAction(Player player, ISlotKey slot) {
        // perform your action
    }
}
```

## Registering

::: code-group

```java [Fabric]
Registry.register(QuickRegistry.ACTION_TYPE, Identifier.of("my_mod", "power"), PowerAction.TYPE);
Registry.register(QuickRegistry.ACTION_TYPE, Identifier.of("my_mod", "reset"), ResetAction.TYPE);
```

```java [NeoForge]
// In your mod constructor
DeferredRegister<ActionType<?>> ACTION_TYPES = DeferredRegister.create(QuickRegistryKeys.ACTION_TYPE, "my_mod");
ACTION_TYPES.register("power", () -> PowerAction.TYPE);
ACTION_TYPES.register("reset", () -> ResetAction.TYPE);
ACTION_TYPES.register(modEventBus);
```

:::

## Using in a datapack

`PowerAction` — the `power` field maps to the codec:

```json
{
  "item": "my_mod:special_item",
  "action": {
    "type": "my_mod:power",
    "power": 5
  }
}
```

`ResetAction` — only `type` is needed:

```json
{
  "item": "my_mod:special_item",
  "action": {
    "type": "my_mod:reset"
  }
}
```

## Using the component in code

To bake the action into an item at registration time, pass it to `Item.Properties`:

::: code-group

```java [Fabric]
Registry.register(
    BuiltInRegistries.ITEM,
    Identifier.of("my_mod", "special_item"),
    new Item(new Item.Properties().component(QuickDataComponents.ITEM_ACTION, new PowerAction(5)))
);
```

```java [NeoForge]
DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "my_mod");
ITEMS.registerSimpleItem(
    "special_item",
    props -> props.component(QuickDataComponents.ITEM_ACTION, new PowerAction(5))
);
ITEMS.register(modEventBus);
```

:::

## The ISlotKey parameter

`onItemAction` receives the `ISlotKey` for the slot the user selected. You can use it to read or modify the item in that slot:

```java
@Override
public void onItemAction(Player player, ISlotKey slot) {
    ItemStack stack = slot.get(player);
    if (stack.isEmpty()) return;

    stack.shrink(1);
    if (stack.isEmpty()) {
        slot.set(player, ItemStack.EMPTY);
    }
}
```

See [Slot System](/reference/slots) for the full `ISlotKey` API and how to create custom slot types.
