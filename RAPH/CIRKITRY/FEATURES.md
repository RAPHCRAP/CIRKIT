# CIRKITRY — Feature & Capability Inventory

> What the app can actually do, based on code analysis.

ck | ✅ Working | `ViewSwitch.update()` — RED when off, BLUEVIOLET when on |
| 3.6 | Truth table generation | ✅ Working | `handleTruthTable()` → `circuit.deepCopy().generateTruthTable()` → displayed in side panel |
| 3.7 | Truth table display in GUI | ✅ Working | `GUIOverlay.updateTruthTable()` renders GridPane with I/O columns and 0/1 rows |

## 4. Composite Components

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 4.1 | Select region by shift+drag | ✅ Working | Hold SHIFT → click clear cell → `EditorMode.HIGHLIGHT` → drag to define rectangle |
| 4.2 | Extract composite from selection | ✅ Working | After highlight, prompts for name → `circuit.extractCompositeFromRect()` |
| 4.3 | Register composite in factory | ✅ Working | `CompositeComponentController.registerCompositeComponent()` → `ComponentFactory.registerCustomType()` |
| 4.4 | Composite appears in palette | ✅ Working | `ComponentFactory.addRegistryListener()` triggers `paletteController.refreshPalette()` |
---

## 1. Circuit Building

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 1.1 | Place logic gates on a grid | ✅ Working | Click component in sidebar → move mouse → click to place. `SelectHandler.enableADD()` → `EditorMode.ADD_COMPONENT` → `circuit.addComponent()` |
| 1.2 | Available gates | ✅ Working | AND, OR, NOT, NAND, NOR, XOR, XNOR — all 7 primitives registered via `PrimitiveBootloader` |
| 1.3 | Available I/O primitives | ✅ Working | Switch (toggleable input), LED (visual output) |
| 1.4 | Move existing components | ✅ Working | Click component → `EditorMode.COMPONENT_SELECTED` → click target cell → `component.moveTo()` |
| 1.5 | Delete components | ✅ Working | Select component → press BACK_SPACE → `circuit.removeComponent()` |
| 1.6 | Visual ghost preview during placement | ✅ Working | `ComponentGhost` shows semi-transparent box at cursor; green=AQUA if valid, RED if blocked |
| 1.7 | Yellow selection box follows cursor | ✅ Working | `selectionBox` — semi-transparent yellow `Box` highlights current grid cell |

## 2. Wiring

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 2.1 | Start a wire from an output pin | ✅ Working | Click unconnected output pin → `EditorMode.WIRE_ADD` → click target cell to place |
| 2.2 | Extend an existing wire | ✅ Working | Click wire node (degree < 4) → `EditorMode.WIRE_NODE_SELECTED` → click to extend via `wire.extendEdge()` |
| 2.3 | L-shaped wire routing | ✅ Working | `WireGhost` renders L-shape with midpoint sphere when endpoint is not aligned |
| 2.4 | Ghost wire preview while drawing | ✅ Working | `WireGhost` shows real-time preview; green=AQUA if valid placement, RED if blocked |
| 2.5 | Wire auto-connects to input pins | ✅ Working | `Wire.updateOccupiedCells()` detects when a wire passes through a cell containing an input pin |
| 2.6 | Delete wire nodes | ✅ Working | Click wire node → press BACK_SPACE → `wire.deleteNode()` |
| 2.7 | Wire rejection: cannot start from input pin | ✅ Working | `Wire` constructor checks `startPin.isOutput()` → throws `IllegalArgumentException` |

## 3. Simulation

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 3.1 | Two-phase signal propagation | ✅ Working | `compute()` → `pin.setNextSignal()` / `commitPins()` → `pin.updateSignal()` / `wire.propagate()` |
| 3.2 | Tick-based simulation | ✅ Working | Press F in RUN mode → `circuit.tick()` iterates all components and wires |
| 3.3 | Toggle switches in run mode | ✅ Working | Click a Switch in RUN mode → `switch.toggle()` |
| 3.4 | LED visual feedback | ✅ Working | `ViewLED.update()` — RED when off, BLUEVIOLET when on |
| 3.5 | Switch visual feedba
## 5. 3D View & Camera

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 5.1 | Perspective 3D view | ✅ Working | `PerspectiveCamera(true)` with near=0.1, far=500000, position z=-3200 |
| 5.2 | Free-fly camera (6DOF) | ✅ Working | `Motion` class — WASD translate, ALT/CONTROL vertical, mouse-drag look, Q/E/R/T/Y/U rotate |
| 5.3 | Textured ground plane | ✅ Working | `Board` — `TriangleMesh` with tiled `tile.jpeg` texture |
| 5.4 | Axis indicator boxes | ✅ Working | RGB colored boxes at ±50 on each axis in `addAxisBoxes()` |
| 5.5 | Component body rendering | ✅ Working | `ViewChip` — blue `Box` with dimensions matching grid size |
| 5.6 | Pin rendering with direction arrows | ✅ Working | `InputPin` / `OutputPin` — box + triangular arrow indicator |
| 5.7 | 3D wire rendering | ✅ Working | `WireModel` — spheres at nodes + cylinders for edges |
| 5.8 | LED/Switch 3D models | ✅ Working | Cylindrical base + bulb/button; color-coded by state |
| 5.9 | Orientation indicator | ✅ Working | `DirectionSphere` — sphere with X/Y/Z colored rings |

## 6. File I/O

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 6.1 | Save circuit to JSON | ✅ Working | File → Save → `CircuitSerializer.serializeFile()` using Gson |
| 6.2 | Load circuit from JSON | ✅ Working | File → Open → `CircuitSerializer.deserializeFile()` → `CircuitBuilder.instantiate()` |
| 6.3 | Save As | ✅ Working | File → Save As → FileChooser |
| 6.4 | New circuit | ✅ Working | File → New → creates empty `Circuit(100, 100)` |
| 6.5 | File tracks current path | ✅ Working | `CircuitFileService.currentFile` — Save reuses path, Save As sets new path |
| 6.6 | Saves custom component definitions | ✅ Working | `CircuitFile` bundles `componentDefs` + `circuitDef` |

## 7. GUI Overlay

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 7.1 | Menu bar (File menu) | ✅ Working | New, Open, Save, Save As, Exit |
| 7.2 | Tools menu | ✅ Working | Export Image (PNG screenshot) |
| 7.3 | Mode switch button (EDIT/RUN) | ✅ Working | Toggles between edit and simulation mode |
| 7.4 | Component palette sidebar | ✅ Working | Slides in/out from left, lists all registered component types |
| 7.5 | Truth table panel | ✅ Working | Slides in/out from right, displays truth table as grid |
| 7.6 | Footer status messages | ✅ Working | Shows transient messages (auto-clear 2s) |
| 7.7 | Footer coordinate display | ✅ Working | Shows current grid position `(x, y)` |

## 8. Export

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 8.1 | Export scene as PNG | ✅ Working | `subScene.snapshot()` → `ImageIO.write()` via `SwingFXUtils` |

## 9. Editor Interaction

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 9.1 | Right-click / middle-click to cancel | ✅ Working | `releaseSelectionHandle()` on SECONDARY/MIDDLE |
| 9.2 | ESC to cancel | ✅ Working | `EventHandles` → `KeyPressHandles()` |
| 9.3 | BACK_SPACE to delete | ✅ Working | Deletes selected wire node or component |
| 9.4 | Component palette dynamically updates | ✅ Working | Registry listener pattern — adding new types auto-refreshes sidebar |
| 9.5 | Collision detection for placement | ✅ Working | `Component.canPlace()` / `canMoveTo()` checks grid occupancy |

## 10. Testing Infrastructure

| # | Feature | Status | Implementation |
|---|---------|--------|----------------|
| 10.1 | JUnit 5 tests | ✅ 9 tests | Model layer tests: Circuit, Component, Pin, Wire, Gates, Factory, Vector3f, Quaternion |
| 10.2 | Maven build | ✅ Working | `mvn compile`, `mvn test`, `mvn javafx:run` |

---

## Not Yet Working / Incomplete

| # | Feature | Status |
|---|---------|--------|
| 1 | RUN mode visual filter | ❌ Stub — `applyRunModeFilter()` and `removeRunModeFilter()` are empty |
| 2 | Circuit deepCopy() | ❓ Called by truth table generation — exists in `Circuit` but wasn't verified working |
| 3 | LED/Switch update on tick | ❓ `ViewLED.update()` and `ViewSwitch.update()` are never called in the animation loop — only on toggle |
| 4 | `com/example/cirkitry/model/components/` | ❌ Excluded from compilation but directory doesn't exist |
| 5 | motionApp.java | ❌ 100% commented out |
| 6 | Orthographic camera | ⚠️ `screenToWorldOrthographic()` is implemented but `ParallelCamera` is never used |
| 7 | `floor.jpg` and `Sprite-0002.ase` | ❌ Unreferenced in code |

---

**Legend:** ✅ = Implemented and wired | ❓ = Implemented but questionable | ❌ = Dead/stub/missing | ⚠️ = Partially implemented
