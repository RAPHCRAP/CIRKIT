# CIRKITRY — Architecture & Codebase Reference

> A digital logic circuit simulator with a 3D JavaFX interface.
> Java 21 + JavaFX 21 + Maven + Gson + JUnit 5.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Directory Layout](#2-directory-layout)
3. [Build System](#3-build-system)
4. [Layer Architecture](#4-layer-architecture)
5. [Model Layer](#5-model-layer)
6. [View Layer (wmodel + graphic)](#6-view-layer)
7. [Handler Layer](#7-handler-layer)
8. [Controller Layer](#8-controller-layer)
9. [Builder Layer](#9-builder-layer)
10. [Business Layer (Serialization)](#10-business-layer)
11. [Utility & Support Classes](#11-utility--support-classes)
12. [Demo / Experimental Classes](#12-demo--experimental-classes)
13. [Test Suite](#13-test-suite)
14. [Resources](#14-resources)
15. [Data Flow & Key Patterns](#15-data-flow--key-patterns)
16. [Running the Project](#16-running-the-project)

---

## 1. Project Overview

**CIRKITRY** is a Logisim-inspired digital logic circuit simulator being built as a 3D JavaFX application. It allows users to:

- Place **logic gates** (AND, OR, NOT, NAND, NOR, XOR, XNOR) on a grid
- Place **input/output primitives** (Switches, LEDs)
- Connect components with **wires** that route through grid cells
- Run **simulation ticks** to propagate boolean signals
- Build **composite components** from sub-circuits
- Save/load circuits as JSON files
- Interact through a **3D perspective view** with full free-fly camera (WASD + mouse look)

The project has ~75 Java source files across 12 packages, 9 JUnit 5 test files, and some experimental/demo prototypes.

---

## 2. Directory Layout

```
CIRKITRY/
├── .vscode/settings.json              # VS Code config (Maven auto-update)
├── .gitignore                          # Ignores target/, .idea/, etc.
├── pom.xml                             # Maven build (Java 21, JavaFX 21)
│
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── cirkitry/               # Main application package
│   │   │   │   ├── Main.java           # Entry point
│   │   │   │   ├── WireTest.java       # Demo circuit factory
│   │   │   │   ├── SelectionManager.java
│   │   │   │   ├── EventHandles.java
│   │   │   │   ├── DirectionSphere.java
│   │   │   │   ├── Motion.java / MotionF.java / motionApp.java
│   │   │   │   ├── Quaternion.java / Vector3f.java
│   │   │   │   │
│   │   │   │   ├── builder/            # CircuitDefinition <-> Circuit conversion
│   │   │   │   ├── business/           # File I/O + JSON serialization
│   │   │   │   ├── controller/         # App, palette, composite controllers
│   │   │   │   ├── debuging/           # Debug printers
│   │   │   │   ├── graphic/            # 3D mesh primitives (Board, Arrow)
│   │   │   │   ├── handler/            # Interaction handling + overlays
│   │   │   │   ├── mathsutil/          # MathUtils, TruthTable
│   │   │   │   ├── model/              # Core domain model
│   │   │   │   │   ├── primitivegates/ # AND, OR, NOT, NAND, NOR, XOR, XNOR
│   │   │   │   │   └── primitives/     # LED, Switch
│   │   │   │   ├── scale/              # Scale constants
│   │   │   │   └── wmodel/             # 3D view models (ViewChip, WireModel, etc.)
│   │   │   │
│   │   │   ├── freefly/Main.java       # Demo: 6DOF camera
│   │   │   ├── gridy/Main.java         # Demo: camera over grid
│   │   │   ├── gui/Main.java           # Demo: 3D scene with HUD
│   │   │   ├── manyobjects/Main.java   # Demo: strategy camera
│   │   │   └── rotatingcuboid/Main.java# Demo: rotating box
│   │   │
│   │   └── resources/
│   │       ├── tile.jpeg               # Board texture (1660 bytes)
│   │       ├── floor.jpg               # Floor texture (163 KB)
│   │       └── Sprite-0002.ase         # Aseprite sprite file (893 bytes)
│   │
│   └── test/java/com/example/cirkitry/
│       ├── CircuitTest.java
│       ├── ComponentFactoryTest.java
│       ├── ComponentTest.java
│       ├── GatesTest.java
│       ├── PinTest.java
│       ├── WireModelTest.java
│       ├── QuaternionRotationTest.java
│       ├── Vector3fDotCrossTest.java
│       └── Vector3fOperationsTest.java
│
└── target/                             # Maven build output (gitignored)
```

---

## 3. Build System

**File:** `pom.xml`

| Setting | Value |
|---------|-------|
| Java | 21 |
| JavaFX | 21.0.4 (controls, fxml, graphics) |
| JavaFX graphics classifier | `win` (Windows native) |
| Gson | 2.10.1 |
| JUnit | 5.10.2 (Jupiter) |
| Main class | `com.example.cirkitry.Main` |
| Compiler plugin | 3.13.0 |
| JavaFX Maven plugin | 0.0.8 |
| Surefire plugin | 3.2.5 (`useModulePath=false`) |

**Important:** `maven-compiler-plugin` excludes `com/example/cirkitry/model/components/**` (a directory that does not exist yet — placeholder for future WIP components).

**Key commands:**

```
mvn compile          # Compile
mvn test             # Run JUnit 5 tests
mvn javafx:run       # Launch the application
mvn clean javafx:run # Clean build + launch
```

---

## 4. Layer Architecture

The code follows a layered architecture with strict dependency direction:

```
 Controller  ──>  Business  ──>  Builder  ──>  Model
    │                 │              │
    └─────────────────┴──────────────┘
                      │
                 [Gson / external]
```

- **Model** — Pure domain logic (no JavaFX dependencies)
- **Builder** — Converts between Model objects and serializable data classes
- **Business** — File I/O, serialization orchestrator
- **Controller** — Wires JavaFX scene, handlers, overlays together
- **Handler** — Mouse/keyboard interaction, overlay UI components
- **View (wmodel + graphic)** — 3D visual representations (depends on Model + JavaFX)

---

## 5. Model Layer

**Package:** `com.example.cirkitry.model`

### 5.1 Core Class Hierarchy

```
Component (abstract)
├── Primitive (abstract, no subcomponents allowed)
│   ├── AndGate
│   ├── OrGate
│   ├── NotGate
│   ├── NandGate
│   ├── NorGate
│   ├── XorGate
│   └── XnorGate
├── AbstractSink (abstract, has 1 input pin)
│   └── Led
├── AbstractSource (abstract, has 1 output pin)
│   └── Switch
└── CompositeComponent (holds subcomponents)
```

### 5.2 `Component` (abstract base)

| Aspect | Detail |
|--------|--------|
| Fields | `name`, `type`, `width`, `height`, `x`, `y`, `inputPins`, `outputPins`, `subcomponents`, `occupiedCells`, `viewGroup` |
| Key Methods | `addInputPin(name)` / `addOutputPin(name)`, `compute()` (abstract), `commitPins()`, `placeInCircuit(x,y,circuit)`, `canPlace()`, `canMoveTo()`, `moveTo()`, `layoutPins()`, `setView()`, `rebuild()`, `stateUpdate()` |
| Grid | All components are positioned on a discrete grid with `x`, `y`, `width`, `height` |

### 5.3 `Primitive` (abstract)

- Extends `Component`
- `compute()` is `final` — delegates to `evaluate()`
- `addSubcomponent()` throws `UnsupportedOperationException`
- Parent of all 7 logic gates

### 5.4 Logic Gates (7 files)

All in `model/primitivegates/`. Each has `width=5`, `height=4`.

| Gate | Inputs | Output | `evaluate()` Logic |
|------|--------|--------|-------------------|
| `AndGate` | inA, inB | outC | `outC.setNextSignal(a && b)` |
| `OrGate` | inA, inB | outC | `outC.setNextSignal(a \|\| b)` |
| `NotGate` | inA | outC | `outC.setNextSignal(!a)` |
| `NandGate` | inA, inB | outC | `outC.setNextSignal(!(a && b))` |
| `NorGate` | inA, inB | outC | `outC.setNextSignal(!(a \|\| b))` |
| `XorGate` | inA, inB | outC | `outC.setNextSignal(a ^ b)` |
| `XnorGate` | inA, inB | outC | `outC.setNextSignal(!(a ^ b))` |

### 5.5 `AbstractSink` → `Led`

- Has a single `INPUT` pin named `"IN"`
- `evaluate()` sets `status = in.getSignal()`
- `layoutPins()` places `in` at `(0, 2)` (left edge, middle)

### 5.6 `AbstractSource` → `Switch`

- Has a single `OUTPUT` pin named `"OUT"`
- `evaluate()` writes `state` to `out`
- `layoutPins()` places `out` at `(4, 2)` (right edge, middle)
- `toggle()` flips state and calls `stateUpdate()`

### 5.7 `Pin`

| Field | Type | Purpose |
|-------|------|---------|
| `relX`, `relY` | int | Position relative to parent component |
| `type` | PinType | INPUT or OUTPUT |
| `parent` | Component | Owning component |
| `signal` | boolean | Stable signal value |
| `nextSignal` | boolean | Value written during tick |
| `connections` | List\<Wire\> | Connected wires |

**Two-phase signal update:** `setNextSignal()` → `updateSignal()` avoids race conditions.

### 5.8 `Wire` / `SimpleWire` / `InternalWire`

| Class | Role |
|-------|------|
| `Wire` | Full wire with multi-node routing, extends `SimpleWire` |
| `SimpleWire` | Base wire with `startPin`, `sinks[]`, signal propagation |
| `InternalWire` | Wire variant used inside composite components |

Key methods: `extendEdge(dx, dy)`, `deleteNode(node)`, `propagate()`, `addSink(pin)`.

### 5.9 `WireNode` & `WireEdge`

- `WireNode` — A point `(x, y)` in grid space with degree tracking
- `WireEdge` — A segment connecting two `WireNode`s

### 5.10 `Cell`

Represents a single grid cell in the `Circuit`. Can hold a `Component`, a `Pin`, or a `Wire` reference.

### 5.11 `Circuit`

Top-level model: holds a 2D grid of `Cell[][]`, list of `Component` objects, and list of `Wire` objects. Key methods: `tick()`, `addComponent()`, `addWire()`, `detachComponent()`, `extractCompositeFromRect()`.

### 5.12 `ComponentFactory` & `PrimitiveBootloader`

- `PrimitiveBootloader.registerAll()` — registers all 7 gates + LED + Switch
- `ComponentFactory.create(typeName)` — creates components by string type name
- `ComponentFactory.registerCustomType(name, definition)` — for user-created composites
- `ComponentFactory.getRegisteredTypes()` — returns all available type names

### 5.13 `CompositeComponent`

Holds a list of subcomponents, with its own pin mapping. Created via `Circuit.extractCompositeFromRect()`.

### 5.14 `ComponentDefinition`

Pure data class for defining a custom component type: holds pin layout, subcomponent references, and wiring.

---

## 6. View Layer

Two sub-packages: `wmodel` (3D view models) and `graphic` (3D mesh primitives).

### 6.1 `SelectableView` (interface)

```java
interface SelectableView {
    Object getModel();
    void onSelect() / onDeselect();
    void rebuild();
    void addGroup(Group g);
    void removeFromSubSceneRoot();
    void update();
    void setColor(Color color);
}
```

Implemented by: `ViewChip`, `ViewLED`, `ViewSwitch`, `WireModel`.

### 6.2 View Implementations

| Class | Renders | Visual |
|-------|---------|--------|
| `ViewChip` | Generic component | Blue box body + InputPin/OutputPin groups at pin positions |
| `ViewLED` | LED | Cylindrical base + bulb (cylinder + sphere), color changes RED/BLUEVIOLET based on status |
| `ViewSwitch` | Switch | Cylindrical base + button (cylinder), color changes RED/BLUEVIOLET based on state |
| `WireModel` | Wire | Spheres at each node + cylinders for each edge segment |

### 6.3 Pin Views

| Class | Visual |
|-------|--------|
| `InputPin` | Box + Arrow (arrow points inward, -X direction) |
| `OutputPin` | Box + Arrow (arrow points outward, -X direction) |

### 6.4 `ViewBuilder`

Factory that creates the appropriate `SelectableView` for each model object. Also adds the `Board` (tiled ground plane) to the 3D scene root `Group`.

### 6.5 `SelectionManager` (wmodel)

Static singleton that tracks the currently selected `SelectableView`. Provides `makeSelectable()` which attaches mouse-click handlers.

### 6.6 `Pos`

Static utility: `setRecPosition(x, y, w, h, node)` positions a JavaFX `Node` at the center of a rectangle in grid coordinates, scaled by `Scale.WCellScale`.

### 6.7 Graphic Primitives

| Class | Description |
|-------|-------------|
| `Board` | `MeshView` — textured ground plane (tiled `tile.jpeg`) |
| `Arrow` | `MeshView` — triangular prism (red by default) for pin direction indicators |
| `applyAction` | Utility that recursively walks a `Group` tree applying a `Consumer<Shape3D>` |

---

## 7. Handler Layer

**Package:** `com.example.cirkitry.handler`

### 7.1 `EditorMode` (enum)

States: `NONE`, `WIRE_NODE_SELECTED`, `COMPONENT_SELECTED`, `WIRE_ADD`, `HIGHLIGHT`, `RUN_MODE`, `ADD_COMPONENT`.

### 7.2 `SelectHandler`

The main interaction controller. Handles:

- **Mouse movement** — grid cell highlighting, ghost previews
- **Mouse clicks** — selection, wire extension, component placement, switch toggle
- **Keyboard** — ESC (release), BACK_SPACE (delete), F (tick)
- **Screen-to-world coordinate conversion** — both perspective and orthographic camera support

Ghost previews: `WireGhost` (L-shaped wire preview), `ComponentGhost` (semi-transparent box), `Highlight` (selection rectangle).

### 7.3 `GUIOverlay`

Builds a 2D JavaFX overlay (menubar, toolbar, sidebar, run panel, footer) on top of the 3D subscene using a `StackPane` with `pickOnBounds(false)`. Features:

- File menu: New/Open/Save/SaveAs/Exit
- Tools: Export Image
- Mode toggle: EDIT ↔ RUN
- Sliding component palette sidebar
- Sliding truth table run panel
- Footer status messages (auto-clear 2s)
- Grid coordinate display

### 7.4 Ghost Classes

| Class | Purpose |
|-------|---------|
| `ComponentGhost` | Semi-transparent box preview when placing a component |
| `WireGhost` | L-shaped wire preview with start/mid/end spheres + cylinder edges |
| `Highlight` | Rectangle selection box for defining composite components |

---

## 8. Controller Layer

**Package:** `com.example.cirkitry.controller`

### 8.1 `AppController`

Main application controller that wires everything together. Owns:

- `SubScene` (1600x900, 3D)
- `PerspectiveCamera` (z=-3200, near=0.1, far=500000)
- `Motion` (6DOF camera controller)
- `SelectHandler` (interaction)
- `GUIOverlay` (UI chrome)
- `ComponentPaletteController` (sidebar)
- `EventHandles` (keyboard state)
- `CircuitFileService` (file I/O)

Handles: new/open/save/saveAs/exportImage, mode switching, truth table generation, component selection from palette.

### 8.2 `ComponentPaletteController`

Manages the sidebar VBox of component buttons. Dynamically rebuilds from `ComponentFactory.getRegisteredTypes()`.

### 8.3 `CompositeComponentController`

Handles registration of user-defined composite components via `registerCompositeComponent(name, builder)`.

---

## 9. Builder Layer

**Package:** `com.example.cirkitry.builder`

### 9.1 `CircuitDefinition`

Pure data / DTO class representing a circuit's serializable structure:

```java
class CircuitDefinition {
    int width, height;
    List<ComponentDef> components;  // type, x, y
    List<WireDef> wires;            // nodes[], edges[]
}
```

Inner classes: `ComponentDef`, `WireDef`, `NodeDef`, `EdgeDef`.

### 9.2 `CircuitBuilder`

Bidirectional converter:

- `instantiate(definition)` → `Circuit` (calls `ComponentFactory.create()` and `Wire.extendEdge()`)
- `toDefinition(circuit)` → `CircuitDefinition` (reverse)

---

## 10. Business Layer (Serialization)

**Package:** `com.example.cirkitry.business`

### 10.1 `CircuitFile`

Top-level serialization wrapper bundling custom `ComponentDefinition`s + `CircuitDefinition`.

### 10.2 `CircuitSerializer`

Static Gson-based serializer: `serialize()` / `deserialize()` (legacy `CircuitDefinition`), `serializeFile()` / `deserializeFile()` (`CircuitFile` with custom component defs).

### 10.3 `CircuitFileService`

Orchestrates load/save workflow: `load(file)` → deserialize → register custom defs → build circuit. `save(file, circuit)` → extract defs → convert → serialize → write.

---

## 11. Utility & Support Classes

### 11.1 `EventHandles`

Keyboard event tracker: attaches to a `Scene`, maintains `HashSet<KeyCode>` of pressed keys, provides `contains(KeyCode)`.

### 11.2 `Scale.WCellScale`

Global cell size constant = `50.0` (world units per grid cell). Used everywhere for positioning.

### 11.3 `MathUtils`

Percentage math utility: `percentageOf()`, `percentageValue()`, `percentageChange()`, `increaseByPercent()`, `decreaseByPercent()`.

### 11.4 `TruthTable`

Models a truth table with named input/output columns and rows. Used in RUN mode to display circuit behavior.

### 11.5 `DirectionSphere`

3D orientation indicator: white sphere with X(red)/Y(blue)/Z(green) rings + yellow forward cylinder.

### 11.6 Motion Controllers

| File | Purpose |
|------|---------|
| `Motion.java` | 6DOF camera controller with pitch-yaw-roll group hierarchy, WASD + mouse look |
| `MotionF.java` | Variant with Affine transforms for compounding rotations, built-in key tracking |
| `motionApp.java` | Commented-out test harness for Motion |

### 11.7 Math Library

| File | Purpose |
|------|---------|
| `Quaternion.java` | Quaternion rotation math (package-private) |
| `Vector3f.java` | 3D vector operations (package-private) |

### 11.8 Debug Utilities

| File | Purpose |
|------|---------|
| `ComponentPrinter.java` | Recursively prints component structure |
| `PinPrinter.java` | Prints pin details with connections |

### 11.9 `WireTest.java`

Contains `demoCircuit()` — builds a sample circuit with AndGate + Led + 2 Switches (used in `Main.java` as starting circuit).

---

## 12. Demo / Experimental Classes

All in `com.example.*` packages, each is a standalone `Application`:

| Class | Demo |
|-------|------|
| `freefly.Main` | 6DOF free-fly camera with Rodrigues rotation |
| `gridy.Main` | Smooth camera over a 2D grid |
| `gui.Main` | 3D rotating cuboid with HUD overlay |
| `manyobjects.Main` | Strategy-game camera over a grid of colored boxes |
| `rotatingcuboid.Main` | Simple rotating cuboid with keyboard camera |

These are **not** part of the main application — they are experimental prototypes for camera and 3D rendering techniques.

---

## 13. Test Suite

**9 JUnit 5 test classes** in `src/test/java/com/example/cirkitry/`:

| Test | Coverage |
|------|----------|
| `CircuitTest` | Cell bounds, add/detach component, extractCompositeFromRect |
| `ComponentFactoryTest` | Primitive registration + creation |
| `ComponentTest` | Pin setup, layout, placement, movement |
| `GatesTest` | AND gate truth table via wired Switches |
| `PinTest` | Signal lifecycle, clear, absolute coordinates |
| `WireModelTest` | Wire construction, propagation, extension, node deletion |
| `QuaternionRotationTest` | Quaternion 90° Z rotation |
| `Vector3fDotCrossTest` | Dot product, cross product, orthogonality |
| `Vector3fOperationsTest` | Add, scale, normalize |

All pass with `mvn test`.

---

## 14. Resources

| File | Location | Usage |
|------|----------|-------|
| `tile.jpeg` | `src/main/resources/` | Board texture (diffuse map for ground plane) |
| `floor.jpg` | `src/main/resources/` | Alternative floor texture (not currently referenced in code) |
| `Sprite-0002.ase` | `src/main/resources/` | Aseprite file (unused in code — design asset) |

---

## 15. Data Flow & Key Patterns

### 15.1 Simulation Tick

```
circuit.tick()
  └─ for each Component:
       compute() → evaluate() → pin.setNextSignal()
  └─ for each Component:
       commitPins() → pin.updateSignal()
  └─ for each Wire:
       propagate()
  └─ for each Component (with SelectableView):
       view.update()
```

### 15.2 Selection Flow

```
Mouse click → SelectHandler.handleSelection()
  → EditorMode dispatch
  → updateMode() / moveComponent() / wireExtensionHandle() / etc.
  → circuit model mutation (addComponent / extendEdge / etc.)
  → ViewBuilder.rebuild()
```

### 15.3 Save/Load Flow

```
SAVE:  Circuit → CircuitBuilder.toDefinition()
       → CircuitFile(customDefs + definition)
       → CircuitSerializer.serializeFile() → Gson → JSON → File

LOAD:  File → JSON → Gson → CircuitFile
       → register custom ComponentDefinitions
       → CircuitBuilder.instantiate(definition)
       → Circuit (with all components and wires)
```

### 15.4 Two-Phase Signal Update

Prevents race conditions during simulation:
1. **Phase 1:** `compute()` reads current `signal`, writes to `nextSignal`
2. **Phase 2:** `commitPins()` calls `updateSignal()` on all pins → `signal = nextSignal`

### 15.5 Main Entry Point

```java
// Main.java
PrimitiveBootloader.registerAll();   // Register AND, OR, NOT, etc.
Circuit circuit = WireTest.demoCircuit();  // Build demo circuit
CircuitDefinition def = CircuitBuilder.toDefinition(circuit);
Circuit c = CircuitBuilder.instantiate(def);
AppController control = new AppController(c);
// JavaFX stage setup...
```

Note: The current code builds a circuit → converts to definition → converts back, which appears redundant (a leftover from testing the serialization round-trip).

---

## 16. Running the Project

```bash
# Compile
mvn compile

# Run tests
mvn test

# Launch application
mvn javafx:run

# Clean build + run
mvn clean javafx:run
```

**Requirements:** JDK 21, Maven 3.x, Windows (for the `win` classifier in javafx-graphics — needs `mac` or `linux` on other platforms).

---

*Generated: June 2026*
