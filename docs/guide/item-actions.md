# Item Actions

An item action defines what happens when a player selects an item in the wheel. There are two ways to assign an action to an item: via a datapack, or via a data component attached to the item.

## Datapack

Create a JSON file at `data/<namespace>/quick/action/<name>.json`. The file specifies which items the action applies to and which action to run:

```json
{
  "item": "minecraft:ender_pearl",
  "action": {
    "type": "quick:use"
  }
}
```

The `item` field accepts any ingredient — a single item ID or a tag prefixed with `#`:

```json
{
  "item": "#my_mod:throwable_items",
  "action": {
    "type": "quick:use"
  }
}
```

Quick ships with a `#quick:simple_action` item tag containing throwable and usable vanilla items (ender pearls, splash potions, firework rockets, elytra, and others). The built-in `data/quick/quick/action/simple.json` applies `quick:use` to everything in that tag. You can add your own items to the tag by creating a tag file in your datapack:

```json
// data/my_mod/tags/item/quick/simple_action.json
{
  "values": [
    "my_mod:special_item"
  ]
}
```

## Data Component

Apply the `quick:item_action` data component directly to an item. The value is a serialized action, identified by the `type` field.

**In JSON** (loot tables, item modifiers, `give` command, custom item definitions):

```json
{
  "components": {
    "quick:item_action": {
      "type": "quick:use"
    }
  }
}
```

**In code** (at item registration time):

::: code-group

```java [Fabric]
// In your ModInitializer
Registry.register(
    BuiltInRegistries.ITEM,
    Identifier.of("my_mod", "my_item"),
    new Item(new Item.Properties().component(QuickDataComponents.ITEM_ACTION, UseAction.INSTANCE))
);
```

```java [NeoForge]
// In your mod constructor
DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "my_mod");
ITEMS.registerSimpleItem(
    "my_item",
    props -> props.component(QuickDataComponents.ITEM_ACTION, UseAction.INSTANCE)
);
ITEMS.register(modEventBus);
```

:::

::: tip
The data component takes priority over datapack entries for the same item stack. Use the component when you need per-item-stack control; use the datapack when you want to cover an entire item type or tag.
:::

## Built-in action types

| ID | Behavior |
|----|----------|
| `quick:use` | Calls `ItemStack.use()` on the item. If the item is already in the main hand slot, it uses it directly. Otherwise it temporarily swaps the item into the main hand, calls `use()`, then restores the original main hand item. The result item ends up back in the original slot, or in the inventory, or on the ground if the inventory is full. |

Custom action types can be added by mods — see [Custom Actions](/guide/custom-actions).
