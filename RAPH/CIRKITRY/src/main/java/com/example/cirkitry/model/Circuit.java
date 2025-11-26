package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public class Circuit {

    private final int width;
    private final int height;

    private final Cell[][] grid;
    private final List<Component> components = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();


    public Circuit(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Cell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }
    }

    // ------------------------------------------------------------
    // Grid Access
    // ------------------------------------------------------------

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return null;
        return grid[x][y];
    }


    // ------------------------------------------------------------
    // Component Management
    // ------------------------------------------------------------

    public boolean addComponent(int gridX, int gridY, Component c) {

    // 1. First compute the component’s proper size

    int w = c.getWidth();
    int h = c.getHeight();

    // 2. Bounds check
    if (gridX < 0 || gridY < 0 ||
        gridX + w > width ||
        gridY + h > height) {
        return false;
    }

    // 3. Collision check using the component's real size
    if (!c.canPlace(gridX, gridY, this)) {
        return false;
    }

    // 4. Now safely place it
    boolean ok = c.placeInCircuit(gridX, gridY, this);
    if (!ok) return false;

    // 5. Finally register it
    components.add(c);
    return true;
}

public boolean removeComponent(Component c) {
    if (c == null) return false;
    if (!components.contains(c)) return false;

    // 1. Remove wires connected to this component
    removeWiresConnectedToComponent(c);

    // 2. Clear occupied cells
    for (Cell cell : c.getOccupiedCells()) {
        Cell gridCell = getCell(cell.getX(), cell.getY());
        if (gridCell != null && gridCell.getComponent() == c) {
            gridCell.setComponent(null);
        }
    }

    // 3. Clear pin cells
    for (Pin pin : c.getInputPins()) {
        clearPinFromGrid(pin);
    }
    for (Pin pin : c.getOutputPins()) {
        clearPinFromGrid(pin);
    }

    // 4. Remove component from list
    components.remove(c);

    return true;
}

private void clearPinFromGrid(Pin pin) {
    if (pin == null) return;
    int x = pin.getAbsoluteX();
    int y = pin.getAbsoluteY();
    Cell cell = getCell(x, y);
    if (cell != null && cell.getPin() == pin) {
        cell.setPin(null);
    }
}

private void removeWiresConnectedToComponent(Component c) {

    List<Wire> toRemove = new ArrayList<>();

    for (Wire w : wires) {

        // CASE 1: wire source pin belongs to component
        if (w.getSource().getParent() == c) {
            toRemove.add(w);
            continue;
        }

        // CASE 2: any sink belongs to component
        boolean sinkOwned = false;
        for (Pin sink : w.getSinks()) {
            if (sink.getParent() == c) {
                sinkOwned = true;
                break;
            }
        }

        if (sinkOwned) {
            toRemove.add(w);
        }
    }

    // REMOVE WIRES + cleanup grid
    for (Wire w : toRemove) {
        removeWire(w);
    }
}




    // ------------------------------------------------------------
    // Wire Management
    // ------------------------------------------------------------

public boolean addWire(Wire w)
{
    if (!w.canPlace(this))
    {
       return false;
    }
     // 4. Now safely place it
    boolean ok = w.placeInCircuit(this);
    if (!ok) return false;

    // 5. Finally register it
    wires.add(w);
    return true;
    
}

public void removeWire(Wire w) {
    if (!wires.contains(w)) return;

    // remove its occupied cells
    for (Cell cell : w.getOccupiedCells()) {
        Cell gridCell = getCell(cell.getX(), cell.getY());
        if (gridCell != null) {
            gridCell.removeWire(w);
        }
    }

    wires.remove(w);
}


    public List<Component> getComponents()
    {
        return components;
    }

    public List<Wire> getWires()
    {
        return wires;
    }


    // ------------------------------------------------------------
    // Simulation Tick
    // ------------------------------------------------------------


    /*
    Notes / Optional Tweaks

Internal wires inside CompositeComponent should still propagate nextSignal → nextSignal for subcomponents; commit happens at the end.

If you ever want delayed / sequential propagation, you could expand commitPins() to handle specific subcomponent order (like topologically).

This design makes your tick entirely deterministic and simple:

compute() → propagate() → commitPins()
     */
    public void tick() {

        // 1. Let all components read their pin inputs
        //    (Pins are typically updated by wires)

        // 2. Let each component compute its output
        for (Component c : components) {
            c.compute();
        }

        // 3. Let each wire propagate signal from output pins to input pins
        for (Wire w : wires) {
            w.propagate();
        }


        for (Component sub : components) sub.commitPins();
    }
}
