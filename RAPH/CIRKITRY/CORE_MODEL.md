# CIRKITRY — Core Logical Model Reference

> Strictly the domain model. No views, no controllers, no handlers.
> Packages: `model`, `model.primitivegates`, `model.primitives`, `builder`, `business`, `mathsutil`.

---

## 1. Class Hierarchy (Inheritance)

```
Component (abstract)                          ← model/Component.java
├── Primitive (abstract)                      ← model/Primitive.java
│   ├── AndGate                               ← model/primitivegates/AndGate.java
│   ├── OrGate                                ← model/primitivegates/OrGate.java
│   ├── NotGate                               ← model/primitivegates/NotGate.java
│   ├── NandGate                              ← model/primitivegates/NandGate.java
│   ├── NorGate                               ← model/primitivegates/NorGate.java
│   ├── XorGate                               ← model/primitivegates/XorGate.java
│   └── XnorGate                              ← model/primitivegates/XnorGate.java
├── AbstractSink (abstract)                   ← model/AbstractSink.java
│   └── Led                                   ← model/primitives/Led.java
├── AbstractSource (abstract)                 ← model/AbstractSource.java
│   └── Switch                                ← model/primitives/Switch.java
└── CompositeComponent                        ← model/CompositeComponent.java
```

---

## 2. `Component` (abstract base)

**File:** `model/Component.java`

### Fields

| Visibility | Name | Type | Default | Purpose |
|-----------|------|------|---------|---------|
| protected | `name` | `String` | ctor arg | Human-readable name |
| protected | `type` | `String` | `"Abstract{Component}"` | Machine type identifier ("AND", "LED", etc.) |
| protected | `width` | `int` | 0 | Width in grid cells |
| protected | `height` | `int` | 0 | Height in grid cells |
| protected | `x` | `int` | 0 | Top-left grid X position |
| protected | `y` | `int` | 0 | Top-left grid Y position |
| protected | `inputPins` | `List<Pin>` | `new ArrayList<>()` | Input pins |
| protected | `outputPins` | `List<Pin>` | `new ArrayList<>()` | Output pins |
| protected | `subcomponents` | `List<Component>` | `new ArrayList<>()` | Child components (composite pattern) |
| protected | `occupiedCells` | `List<Cell>` | `new ArrayList<>()` | Grid cells this component occupies |

### Constructors

```java
public Component(String name)   // sets this.name = name, this.type = "Abstract{Component}"
```

### Methods — what each level adds

| Method | Visibility | Return | Abstract? | Default Behavior | Overridden By |
|--------|-----------|--------|-----------|-----------------|---------------|
| `addInputPin(name)` | public | `Pin` | No | Creates `Pin(INPUT, this)`, adds to `inputPins` list, returns it | — |
| `addOutputPin(name)` | public | `Pin` | No | Creates `Pin(OUTPUT, this)`, adds to `outputPins` list, returns it | — |
| `getInputPins()` | public | `List<Pin>` | No | Returns `inputPins` | — |
| `getOutputPins()` | public | `List<Pin>` | No | Returns `outputPins` | — |
| `addSubcomponent(comp)` | public | void | No | Adds to `subcomponents` list | **Primitive**: throws `UnsupportedOperationException` |
| | | | | | **AbstractSink**: throws `UnsupportedOperationException` |
| | | | | | **AbstractSource**: throws `UnsupportedOperationException` |
| | | | | | **CompositeComponent**: allows (normal add) |
| `getSubcomponents()` | public | `List<Component>` | No | Returns `subcomponents` | — |
| `commitPins()` | public | void | No | Calls `updateSignal()` on all `inputPins` + `outputPins`, recurses into subcomponents first | — |
| `compute()` | public | void | **YES** | — | **Primitive**: `final` → calls `evaluate()` |
| | | | | | **AbstractSink**: → calls `evaluate()` |
| | | | | | **AbstractSource**: → calls `evaluate()`, then `out.setNextSignal(state)` |
| | | | | | **CompositeComponent**: iterates subcomponents calling `compute()`, then propagates internal wires |
| `computePreferredSize()` | protected | `Point2D` | No | `max(inputPins.size(), outputPins.size(), height)` x `max(3, width)` | — |
| `applyPreferredSize()` | protected | void | No | Sets `width`/`height` from `computePreferredSize()` | — |
| `layoutPins()` | protected | void | No | Distributes input pins at `x=0` and output pins at `x=width-1`, evenly spaced vertically | **Led**: `in.setRelative(0, 2)` |
| | | | | | **Switch**: `out.setRelative(4, 2)` |
| `canPlace(gridX, gridY, circuit)` | public | boolean | No | Checks all cells in the component's bounding box are free via `cell.canPlaceComponent()` | — |
| `canMoveTo(gridX, gridY, circuit)` | public | boolean | No | Checks all cells via `cell.canMoveComponent(this)` (allows own cells) | — |
| `moveTo(gridX, gridY, circuit)` | public | boolean | No | Calls `detachComponent` → updates `x,y` → calls `addComponent` | — |
| `placeInCircuit(gridX, gridY, circuit)` | public | boolean | No | `applyPreferredSize()` → `pinConstraint()` → `canPlace()` → sets x,y → `layoutPins()` → `updateOccupiedCells()` → `updatePinCells()` | — |
| `pinConstraint()` | private | boolean | No | Checks all pins lie within component bounds | — |
| `getName()` | public | String | No | Returns `name` | — |
| `getType()` | public | String | No | Returns `type` | — |
| `getX()` / `getY()` | public | int | No | Returns `x` / `y` | — |
| `getWidth()` / `getHeight()` | public | int | No | Calls `applyPreferredSize()` first, then returns | — |

### Abstract methods

| Method | Signature | Who implements |
|--------|-----------|----------------|
| `compute()` | `public abstract void compute()` | **Primitive**: `final` → `evaluate()` |
| | | **AbstractSink**: → `evaluate()` |
| | | **AbstractSource**: → `evaluate()` + `out.setNextSignal(state)` |
| | | **CompositeComponent**: loop + propagate |

---

## 3. `Primitive` (abstract) — extends `Component`

**File:** `model/Primitive.java`

### Overrides

| Method | Change |
|--------|--------|
| `compute()` | **`final`** — calls `evaluate()` |
| `addSubcomponent()` | Throws `UnsupportedOperationException` — primitives are atomic |

### Adds

| Method | Visibility | Return | Abstract? | Purpose |
|--------|-----------|--------|-----------|---------|
| `evaluate()` | protected | void | **YES** | Subclasses implement their boolean logic here |

### Inherited (NOT overridden)

| Method | Why |
|--------|-----|
| `layoutPins()` | Uses default centered vertical layout from `Component` |
| `placeInCircuit()` | Uses default flow from `Component` |
| `commitPins()` | Uses default from `Component` |

---

## 4. `AbstractSink` (abstract) — extends `Component`

**File:** `model/AbstractSink.java`

### Fields

| Visibility | Name | Type | Purpose |
|-----------|------|------|---------|
| protected | `in` | `Pin` | Single input pin (initialized in constructor via `addInputPin("IN")`) |
| protected | `status` | `boolean` | Current state (e.g., LED on/off) |

### Overrides

| Method | Change |
|--------|--------|
| `compute()` | Calls `evaluate()` (not final — subclasses can vary evaluation pattern) |
| `addSubcomponent()` | Throws `UnsupportedOperationException` |

### Adds

| Method | Visibility | Return | Abstract? | Purpose |
|--------|-----------|--------|-----------|---------|
| `evaluate()` | protected | void | **YES** | Subclasses define how they respond to input |
| `getStatus()` | public | `boolean` | No | Returns `status` |
| `getIn()` | public | `Pin` | No | Returns `in` |

### Concrete: `Led` (`model/primitives/Led.java`)

| Aspect | Detail |
|--------|--------|
| `name` | `"LED"` |
| `type` | `"LED"` |
| `width`, `height` | 5, 5 |
| `status` | initialized `false` |
| `evaluate()` | `status = in.getSignal()` |
| `layoutPins()` | `in.setRelative(0, 2)` — pin at left edge, row 2 |

---

## 5. `AbstractSource` (abstract) — extends `Component`

**File:** `model/AbstractSource.java`

### Fields

| Visibility | Name | Type | Purpose |
|-----------|------|------|---------|
| protected | `out` | `Pin` | Single output pin (initialized via `addOutputPin("OUT")`) |
| protected | `state` | `boolean` | Current value (`false` by default) |

### Overrides

| Method | Change |
|--------|--------|
| `compute()` | Calls `evaluate()`, then `out.setNextSignal(state)` — **writes state to pin** |
| `addSubcomponent()` | Throws `UnsupportedOperationException` |

### Adds

| Method | Visibility | Return | Abstract? | Purpose |
|--------|-----------|--------|-----------|---------|
| `evaluate()` | protected | void | **YES** | Subclasses define state derivation |
| `getState()` | public | `boolean` | No | Returns `state` |
| `getOut()` | public | `Pin` | No | Returns `out` |
| `setState(newState)` | public | void | No | External control to set state |

### Concrete: `Switch` (`model/primitives/Switch.java`)

| Aspect | Detail |
|--------|--------|
| `name` | `"SWITCH"` |
| `type` | `"SWITCH"` |
| `width`, `height` | 5, 5 |
| `state` | initialized `false` |
| `evaluate()` | `out.setNextSignal(state)` — just propagates current state |
| `layoutPins()` | `out.setRelative(4, 2)` — pin at right edge, row 2 |
| `toggle()` | `state = !state; stateUpdate()` |

### Critical behavioral diff: `compute()`

| Class | `compute()` does |
|-------|-----------------|
| **Primitive** | `evaluate()` — reads input pins, writes to output pins via `setNextSignal()` |
| **AbstractSource** | `evaluate()` then **`out.setNextSignal(state)`** — forces the output pin to the source's internal state |
| **AbstractSink** | `evaluate()` — reads input pin, updates internal status |
| **CompositeComponent** | loop: `sub.compute()` for all subcomponents, then `internalWire.propagate()` for all internal wires |

---

## 6. `CompositeComponent` — extends `Component`

**File:** `model/CompositeComponent.java`

### Fields

| Visibility | Name | Type | Purpose |
|-----------|------|------|---------|
| protected | `internalWires` | `List<InternalWire>` | Wires connecting subcomponent pins inside the composite |

### Overrides

| Method | Change |
|--------|--------|
| `addSubcomponent(comp)` | Allows it (normal add to list) — **no exception thrown** |
| `compute()` | Loop: `sub.compute()` for all subcomponents, then `wire.propagate()` for all internal wires |

### Adds

| Method | Visibility | Return | Purpose |
|--------|-----------|--------|---------|
| `getInternalWires()` | public | `List<InternalWire>` | Returns internal wires |
| `connect(src, dst)` | public | void | Creates `InternalWire(src, dst)` and adds to `internalWires` |

### Inherited (inherits default from Component)

| Method | Notes |
|--------|-------|
| `layoutPins()` | Default centered vertical layout |
| `commitPins()` | Default — recurses into subcomponents |

---

## 7. More gates — all extend `Primitive`

### Two-input gates (AND, OR, NAND, NOR, XOR, XNOR)

| Gate | Pins | `evaluate()` |
|------|------|-------------|
| `AndGate` | inA, inB, outC | `outC.setNextSignal(a && b)` |
| `OrGate` | inA, inB, outC | `outC.setNextSignal(a \|\| b)` |
| `NandGate` | inA, inB, outC | `outC.setNextSignal(!(a && b))` |
| `NorGate` | inA, inB, outC | `outC.setNextSignal(!(a \|\| b))` |
| `XorGate` | inA, inB, outC | `outC.setNextSignal(a ^ b)` |
| `XnorGate` | inA, inB, outC | `outC.setNextSignal(!(a ^ b))` |

### One-input gate

| Gate | Pins | `evaluate()` |
|------|------|-------------|
| `NotGate` | inA, outC | `outC.setNextSignal(!a)` |

### Common pattern across all 7

```
Constructor:
  super("NAME");       // sets name
  this.type = "NAME";  // sets type
  this.width = 5;
  this.height = 4;
  inA = addInputPin("A");
  inB = addInputPin("B"); // (NotGate omits this)
  outC = addOutputPin("C");

evaluate(): reads signals from input pins, writes nextSignal to output pin

Optional pin getters: getInA(), getInB(), getOutC()

Does NOT override: layoutPins(), placeInCircuit(), commitPins(), addSubcomponent() (inherits throws from Primitive)
```

---

## 8. `Pin`

**File:** `model/Pin.java`

### Fields

| Type | Name | Purpose |
|------|------|---------|
| `int` | `relX`, `relY` | Position relative to parent component's top-left |
| `PinType` | `type` | `INPUT` or `OUTPUT` |
| `Component` | `parent` | The component that owns this pin |
| `boolean` | `signal` | Current stable signal (what a component reads) |
| `boolean` | `nextSignal` | Value written during a tick, committed by `updateSignal()` |
| `List<Wire>` | `connections` | Wires attached to this pin |

### Key methods

| Method | Details |
|--------|---------|
| `getSignal()` | Returns `signal` — the stable value |
| `setNextSignal(s)` | Writes `nextSignal = s` — called during compute/propagate |
| `updateSignal()` | `signal = nextSignal` — called by `Component.commitPins()` at end of tick |
| `addConnection(w)` | Bidirectional: adds wire to list |
| `removeConnection(w)` | Removes wire from list |
| `getAbsoluteX/Y()` | `parent.x + relX` (or `parent.y + relY`) |
| `setRelative(x, y)` | Sets `relX, relY` |
| `clear()` | Empties connections, resets `signal=false`, `nextSignal=false` |

### Signal lifecycle

```
compute() → pin.setNextSignal(value)        Phase 1: write tentative value
              ... wires propagate ...
commitPins() → pin.updateSignal()           Phase 2: make it stable
```

---

## 9. `PinType` (enum)

**File:** `model/PinType.java`

```java
public enum PinType { INPUT, OUTPUT }
```

---

## 10. `Wire`

**File:** `model/Wire.java`

### Fields

| Type | Name | Purpose |
|------|------|---------|
| `Pin` | `source` | Single source pin (must be OUTPUT) |
| `List<Pin>` | `sinks` | Sink pins (must be INPUT) |
| `List<WireNode>` | `nodes` | Ordered routing points (including endpoints) |
| `List<WireEdge>` | `edges` | Straight segments connecting nodes |
| `List<Cell>` | `occupiedCells` | Grid cells this wire passes through |

### Constructor

```java
Wire(int x, int y, Pin source)
// Checks source.isOutput() — throws IllegalArgumentException if not
// Creates root WireNode at (x, y), adds source.addConnection(this)
```

### Key methods

| Method | Details |
|--------|---------|
| `addSink(Pin)` | Checks `sink.isInput()` — throws if not. Adds to `sinks`, calls `sink.addConnection(this)` |
| `removeSink(Pin)` | Removes from sinks + disconnects from pin |
| `propagate()` | `source.getSignal()` → `sink.setNextSignal(value)` for each sink |
| `extendEdge(node, x2, y2, circuit)` | Extends wire from node to (x2,y2). Handles straight lines and L-shapes (via midpoint). Validates via `canPlaceLine()`. Creates nodes/edges, updates occupied cells. Auto-attaches to input pins along the path |
| `deleteNode(node, circuit)` | Removes a leaf node (degree must be 1). Removes edge, releases cells, clears grid references |
| `canPlace(circuit)` | Validates all edges don't conflict with existing cells |
| `canExtend(node, x2, y2, circuit)` | Preview validation — checks if extension is possible |
| `getSource()` / `getSinks()` | Accessors |
| `getNodes()` / `getEdges()` | Accessors |

### Edge and Node management (private)

| Method | Purpose |
|--------|---------|
| `findNode(x, y)` | Lookup existing node |
| `ensureNode(x, y)` | Find or create node |
| `addEdge(a, b)` | Creates `WireEdge`, enforces degree ≤ 4 per node, prevents duplicates |

### Node degree limits

Each `WireNode` has a `degree` (0–4). `addEdge()` refuses edges when either endpoint has degree ≥ 4. This enforces that at most 4 wire segments can meet at a point (up/down/left/right).

### Occpancy lifecycle

```
placeInCircuit(circuit):
  canPlaceWire() → validates all cells via cell.canPlaceWire(this)
  updateOccupiedCells() → clears old, sets new cell occupancy,
                          places nodes in cells, auto-attaches to input pins
```

---

## 11. `SimpleWire`

**File:** `model/SimpleWire.java`

Pure data class — **not** a `Wire` subclass. Used as an intermediate representation during composite component extraction.

```java
public class SimpleWire {
    public final Pin src;
    public final Pin dst;
    public final Wire original;     // the real wire this edge came from
}
```

---

## 12. `InternalWire`

**File:** `model/InternalWire.java`

Used inside `CompositeComponent` for connections between subcomponents.

```java
public class InternalWire {
    private final Pin src;
    private final Pin dst;

    public void propagate() {
        dst.setNextSignal(src.getSignal());
    }
}
```

Note: only copies `signal`, not `nextSignal` — so internal wires propagate the stable value after subcomponents have committed.

---

## 13. `WireNode`

**File:** `model/WireNode.java`

```java
public class WireNode {
    private final Wire wire;     // owning wire
    private int degree;          // 0-4, how many edges connect here
    private int x, y;            // grid position

    // incrementDegree() / decrementDegree() — no upper bound enforcement here
    // (enforced in Wire.addEdge())
}
```

---

## 14. `WireEdge`

**File:** `model/WireEdge.java`

```java
public class WireEdge {
    private final WireNode a, b;

    // Constructor validates: a and b must be aligned (same X or same Y)
    // Otherwise throws IllegalArgumentException
}
```

---

## 15. `WireSegment`

**File:** `model/WireSegment.java`

Utility class for computing covered cells along a straight segment.

```java
public class WireSegment {
    private final int x1, y1, x2, y2;

    public List<Cell> getCoveredCells(Circuit circuit)
    // Returns all cells along the line (inclusive)
}
```

---

## 16. `Cell`

**File:** `model/Cell.java`

A single cell in the circuit grid.

### Fields

| Type | Name | Purpose |
|------|------|---------|
| `int` | `x`, `y` | Grid position |
| `Component` | `component` | Component occupying this cell (or null) |
| `Pin` | `pin` | Pin at this cell (or null) |
| `List<Wire>` | `wires` | Wires passing through this cell |
| `WireNode` | `node` | Wire routing node at this cell (or null) |

### Occupancy rules

| Method | Returns true when... |
|--------|---------------------|
| `isEmpty()` | No component, no pin, no wires |
| `isClear()` | No node, no component, no wire, no pin |
| `hasComponent()` / `hasPin()` / `hasWire()` / `hasNode()` | Respective field is non-null/non-empty |
| `canPlaceComponent()` | No component AND no pin AND no wires AND no node (completely empty) |
| `canMoveComponent(comp)` | Blocked only by another component (≠ comp) or foreign wires/nodes |
| `canPlaceWire(wire)` | Complex logic — allows source pin, unconnected input pins, own nodes; blocks foreign components, output pins that aren't the source, connected input pins |

---

## 17. `Circuit`

**File:** `model/Circuit.java`

### Fields

| Type | Name | Purpose |
|------|------|---------|
| `int` | `width`, `height` | Grid dimensions |
| `Cell[][]` | `grid` | 2D grid (centered — index = worldCoord + dimension/2) |
| `List<Component>` | `components` | All components in the circuit |
| `List<Wire>` | `wires` | All wires in the circuit |

### Key methods

| Method | Details |
|--------|---------|
| `getCell(x, y)` | Translates world coords to grid index. Returns null if out of bounds |
| `addComponent(x, y, c)` | Bounds check → collision check → `c.placeInCircuit()` → register |
| `detachComponent(c)` | Removes connected wires, clears cells/pins, removes from list |
| `removeComponent(c)` | `detachComponent()` + `c.removeView()` |
| `addWire(w)` | `canPlace()` → `placeInCircuit()` → register |
| `removeWire(w)` | Clears source, clears sinks, clears edge cells from grid, removes from list |
| `getInputComponents()` | All `AbstractSource` instances (used for truth table) |
| `getOutputComponents()` | All `AbstractSink` instances (used for truth table) |
| `tick()` | **`compute()` on all components → `propagate()` on all wires → `commitPins()` on all components → `stateUpdate()` on all components** |
| `settle()` | Runs `tick()` 10 times to stabilize |
| `deepCopy()` | `CircuitBuilder.instantiate(CircuitBuilder.toDefinition(this))` — serialize round-trip |
| `generateTruthTable()` | Iterates all 2^n input combinations, runs `settle()`, collects outputs, returns `TruthTable` |
| `extractCompositeFromRect(x1,y1,x2,y2,name)` | Classifies internal/entering/leaving wires, checks acyclicity (topological sort), builds a `ComponentBuilder` with cloned subcomponents and external pin mapping |

---

## 18. `ComponentFactory`

**File:** `model/ComponentFactory.java`

Static registry.

```java
registry: Map<String, Supplier<Component>>     // type name → factory
customDefinitions: Map<String, ComponentDefinition> // user composites
listeners: List<Runnable>                        // registry change listeners
```

| Method | Purpose |
|--------|---------|
| `registerPrimitive(name, factory)` | Registers a simple `Supplier<Component>` |
| `registerCustomType(name, definition)` | Registers a composite: saves definition + creates `() -> ComponentBuilder.instantiate(def)` factory |
| `create(typeName)` | Looks up factory, calls `get()`. Throws `IllegalArgumentException` if unknown |
| `getRegisteredTypes()` | Returns all keys |
| `getAllCustomDefs()` | Returns list of all custom `ComponentDefinition`s (for saving) |
| `addRegistryListener(r)` | Adds change listener (used by palette auto-refresh) |

---

## 19. `PrimitiveBootloader`

**File:** `model/PrimitiveBootloader.java`

Static registration of all built-in types:

```java
AND, OR, NOT, NAND, NOR, XOR, XNOR, LED, SWITCH
```

---

## 20. `ComponentDefinition` & `ComponentBuilder`

### `ComponentDefinition` (`model/ComponentDefinition.java`)

Pure data — serializable definition of a custom component type.

```java
class ComponentDefinition {
    String name;
    int inputCount, outputCount;
    List<SubcomponentDef> subcomponents;
    List<ConnectionDef> connections;

    // Inner types:
    class SubcomponentDef { String type, id; }
    class ConnectionDef  { PinRef src, dst; }
    class PinRef         { String componentId; int pinIndex; }
}
```

Special `componentId` values: `"input"` and `"output"` refer to the composite's own external pins. Any other string refers to a named subcomponent.

### `ComponentBuilder` (`model/ComponentBuilder.java`)

Builder pattern for assembling a `CompositeComponent` programmatically or from a `ComponentDefinition`.

| Method | Purpose |
|--------|---------|
| `addInput(name)` | Creates external input pin on the composite |
| `addOutput(name)` | Creates external output pin |
| `addSubcomponent(comp)` | Adds internal component |
| `connect(src, dst)` | Creates `InternalWire` between two pins |
| `build()` | Returns the `CompositeComponent` |
| `buildDefinition()` | Converts to `ComponentDefinition` (serializable) |
| `static instantiate(def)` | Builds a `CompositeComponent` from a `ComponentDefinition` |

---

## 21. Composite Extraction Algorithm

In `Circuit.extractCompositeFromRect()`:

```
1. Collect all components inside the rectangle
2. Flatten all real Wire connections into SimpleWire edges (src → dst)
3. Classify each edge:
   - internal: src and dst both inside rectangle
   - entering: src outside, dst inside
   - leaving:  src inside, dst outside
4. Cycle-check internal edges via topological sort (Kahn's algorithm)
5. Clone each internal component via ComponentFactory.create()
6. Clone pin mappings (orig → clone)
7. Rebuild internal wiring via builder.connect(clonedSrc, clonedDst)
8. Create external input pins for entering edges, wire to cloned dst pins
9. Create external output pins for leaving edges, wire from cloned src pins
10. Return the ComponentBuilder (NOT YET INSERTED into the circuit)
```

---

## 22. Simulation Tick (precise order)

From `Circuit.tick()`:

```
Phase 1: compute()
  → For each Component:
       Component.compute()
         ├── Primitive: evaluate() — reads input pins' signal,
         │                 writes output pins' nextSignal
         ├── AbstractSink: evaluate() — reads in.getSignal(),
         │                  updates internal status
         ├── AbstractSource: evaluate() + out.setNextSignal(state)
         │                   — forces output pin to current state
         └── CompositeComponent:
               for each sub: sub.compute() (recursive)
               for each internalWire: internalWire.propagate()
                                       → dst.setNextSignal(src.getSignal())

Phase 2: propagate()
  → For each Wire:
       Wire.propagate() — source.getSignal() → sink.setNextSignal() for each sink

Phase 3: commitPins()
  → For each Component:
       Component.commitPins()
         → recursively commits subcomponents first
         → pin.updateSignal() on all inputPins + outputPins
           (makes nextSignal → signal)

Phase 4: stateUpdate()
  → For each Component:
       Component.stateUpdate() — triggers view refresh
```

---

## 23. Summary: What each concrete class overrides

| Class | `compute()` | `evaluate()` | `addSubcomponent()` | `layoutPins()` | Other |
|-------|-------------|--------------|---------------------|----------------|-------|
| `Component` | abstract | — | normal add | default centered | — |
| `Primitive` | **final** → `evaluate()` | abstract | **throws** | NOT overridden | — |
| `AbstractSink` | → `evaluate()` | abstract | **throws** | NOT overridden | — |
| `AbstractSource` | → `evaluate()` + `out.setNextSignal(state)` | abstract | **throws** | NOT overridden | `setState()` |
| `AndGate` | inherited (final) | `c = a && b` | inherited (throws) | inherited | pin getters |
| `OrGate` | inherited (final) | `c = a \|\| b` | inherited (throws) | inherited | pin getters |
| `NotGate` | inherited (final) | `c = !a` | inherited (throws) | inherited | pin getters |
| `NandGate` | inherited (final) | `c = !(a&&b)` | inherited (throws) | inherited | pin getters |
| `NorGate` | inherited (final) | `c = !(a\|\|b)` | inherited (throws) | inherited | pin getters |
| `XorGate` | inherited (final) | `c = a ^ b` | inherited (throws) | inherited | pin getters |
| `XnorGate` | inherited (final) | `c = !(a^b)` | inherited (throws) | inherited | pin getters |
| `Led` | inherited | `status = in.getSignal()` | inherited (throws) | `in at (0,2)` | — |
| `Switch` | inherited | `out.setNextSignal(state)` | inherited (throws) | `out at (4,2)` | `toggle()` |
| `CompositeComponent` | loop subs + propagate internal | — | **allows** (no throw) | NOT overridden | `connect()`, `getInternalWires()` |

---

## 24. Key Design Patterns

| Pattern | Where |
|---------|-------|
| **Composite** | `Component` contains `List<Component> subcomponents`. `Primitive`/`AbstractSink`/`AbstractSource` are leaf nodes (throw on add). `CompositeComponent` is the composite node (allows add + recursive compute). |
| **Template Method** | `Component.compute()` is abstract. `Primitive` makes it `final` and delegates to `evaluate()`. Concrete gates only implement `evaluate()`. |
| **Two-phase signal update** | `Pin.setNextSignal()` (write) / `Pin.updateSignal()` (commit). Prevents race conditions during simulation. |
| **Factory** | `ComponentFactory` with `Supplier<Component>` registry. `PrimitiveBootloader` populates it. |
| **Builder** | `ComponentBuilder` constructs `CompositeComponent` with its subcomponents and internal wiring. |
| **Strategy** | `Cell.canPlaceComponent()` / `canMoveComponent()` / `canPlaceWire()` — different placement rules for different scenarios. |
| **DTO** | `ComponentDefinition`, `CircuitDefinition`, `CircuitFile` — pure data classes for serialization. |

---

*Generated: June 2026*
