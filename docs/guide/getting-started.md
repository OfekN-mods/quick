# Introduction

Quick is a Minecraft mod that adds a radial selection wheel players can use to activate items from their inventory. When an item is selected in the wheel, its configured action runs. The built-in `quick:use` action calls `ItemStack.use()` on the item, letting players throw ender pearls, drink potions, or fire firework rockets without putting the item in their hand first.

## API overview

Quick exposes an API for mod authors to:

- Assign actions to items via datapack or data component (no code needed)
- Create custom action types that run server-side logic
- Expose modded inventory slots to the wheel
- Open custom radial wheels from client-side code

**API packages:**

| Package | Side | Purpose |
|---------|------|---------|
| `com.ofekn.quick.api.common` | Server | Action types, slot registry, data components |
| `com.ofekn.quick.api.client` | Client | Opening wheels, wheel layouts |

## What's next

- [Item Actions](/guide/item-actions) — assign actions to items via datapack or data component
- [Custom Actions](/guide/custom-actions) — implement and register your own action types
- [Slot System](/reference/slots) — extend slot access for modded inventories
- [Custom Wheels](/reference/custom-wheels) — open custom radial wheels from code
