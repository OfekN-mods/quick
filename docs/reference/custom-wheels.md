# Custom Wheels

`QuickClientApi.openWheel(WheelData)` opens the radial wheel screen. It does nothing if another screen is already open or if the local player is null. Call it from client-side code only (inside `@Environment(EnvType.CLIENT)` methods or equivalent).

```java
import com.ofekn.quick.api.client.QuickClientApi;

QuickClientApi.openWheel(myWheelData);
```

## WheelData

`WheelData` is an interface that drives the wheel:

```java
public interface WheelData {
    List<IWheelOption> options(); // entries shown in the wheel
    Status status();              // current lifecycle state
}
```

`Status` controls the wheel's lifecycle. The wheel polls `status()` every tick, so you can drive it externally — for example, close it when a keybind is released:

| Value | Effect |
|-------|--------|
| `SELECTING` | Normal operation — wheel stays open |
| `FINISH_SELECT` | Confirm the current selection and close |
| `CANCEL` | Close without running any action |

## IWheelOption

Each entry in the wheel implements `IWheelOption`:

```java
public interface IWheelOption {
    void onSelect();
    void extract(GuiGraphicsExtractor graphics);
    void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY);
}
```

- `onSelect` — called when the user confirms this option (mouse release or `FINISH_SELECT`)
- `extract` — render the icon centered at `(0, 0)`. The graphics matrix is already translated to the icon center.
- `extractTooltip` — render a tooltip at the mouse position. Called only for the currently highlighted option, and only once the open animation has finished.

For item icons and tooltips:

```java
@Override
public void extract(GuiGraphicsExtractor graphics) {
    graphics.item(stack, -8, -8);
}

@Override
public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
}
```

## Full example

```java
record ItemOption(ItemStack stack, Font font, Runnable action) implements IWheelOption {
    @Override
    public void onSelect() { action.run(); }

    @Override
    public void extract(GuiGraphicsExtractor graphics) {
        graphics.item(stack, -8, -8);
    }

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
    }
}

// Build the wheel data
List<IWheelOption> options = List.of(
    new ItemOption(new ItemStack(Items.ENDER_PEARL), font, () -> doSomething()),
    new ItemOption(new ItemStack(Items.FIREWORK_ROCKET), font, () -> doSomethingElse())
);

QuickClientApi.openWheel(new WheelData() {
    @Override public List<IWheelOption> options() { return options; }
    @Override public WheelData.Status status() { return WheelData.Status.SELECTING; }
});
```

To close the wheel from outside — for example when the player releases a keybind — use a mutable status holder:

```java
class MyWheelData implements WheelData {
    private final List<IWheelOption> options;
    private Status status = Status.SELECTING;

    MyWheelData(List<IWheelOption> options) { this.options = options; }

    @Override public List<IWheelOption> options() { return options; }
    @Override public Status status() { return status; }

    public void cancel() { status = Status.CANCEL; }
    public void confirm() { status = Status.FINISH_SELECT; }
}

// On keybind release:
myWheelData.confirm();
```

## Wheel layouts (experimental)

The wheel layout controls how the polygonal segments are arranged. Custom layouts can be registered to `QuickClientRegistry.WHEEL_LAYOUT`. The user can cycle through available layouts by right-clicking while the wheel is open.

::: warning
`WheelLayout` and `WheelPolygon` are marked `@ApiStatus.Experimental` and may change without notice.
:::

```java
import com.ofekn.quick.api.client.WheelLayout;
import com.ofekn.quick.api.client.WheelPolygon;

public class MyLayout implements WheelLayout {
    @Override
    public WheelPolygon[] polygons(int numOptions) {
        // Return one WheelPolygon per option.
        // Each WheelPolygon has a Vector2f[] of boundary points and a Vector2f center.
        // All coordinates are relative to the screen center (0, 0).
    }
}
```

::: code-group

```java [Fabric]
// In your ClientModInitializer
Registry.register(
    QuickClientRegistry.WHEEL_LAYOUT,
    Identifier.of("my_mod", "my_layout"),
    new MyLayout()
);
```

```java [NeoForge]
// In your client mod constructor
DeferredRegister<WheelLayout> WHEEL_LAYOUTS = DeferredRegister.create(QuickClientRegistryKeys.WHEEL_LAYOUT, "my_mod");
WHEEL_LAYOUTS.register("my_layout", MyLayout::new);
WHEEL_LAYOUTS.register(modEventBus);
```

:::
