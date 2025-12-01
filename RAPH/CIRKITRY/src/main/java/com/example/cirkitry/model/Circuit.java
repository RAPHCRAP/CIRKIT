package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

import com.example.cirkitry.scale.Scale;

public class Circuit {

    private final int width;
    private final int height;

    private final Cell[][] grid;
    private final List<Component> components = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();


    public Circuit(int width, int height) {
        this.width = Scale.WSize;
        this.height = Scale.WSize;

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

    public Cell getCell(int x, int y) 
    {
        int indexX = x + width / 2;
        int indexY = y + height / 2;

        if (indexX < 0 || indexX >= width || indexY < 0 || indexY >= height)
        {
            return null; 
        }

        return grid[indexX][indexY];
    }


    // ------------------------------------------------------------
    // Component Management
    // ------------------------------------------------------------
public boolean addComponent(int worldX, int worldY, Component c) {

    int w = c.getWidth();
    int h = c.getHeight();

    // Calculate world bounds of component
    int minX = worldX;
    int maxX = worldX + w - 1;

    int minY = worldY;
    int maxY = worldY + h - 1;

    int halfW = width  / 2;
    int halfH = height / 2;

    // ----- 1. Bounds check in world coordinates -----
    if (minX < -halfW || maxX >= halfW ||
        minY < -halfH || maxY >= halfH)
    {
        return false;
    }

    // ----- 2. Collision check -----
    if (!c.canPlace(worldX, worldY, this)) {
        return false;
    }

    // ----- 3. Actually place the component -----
    boolean ok = c.placeInCircuit(worldX, worldY, this);
    if (!ok) return false;

    // ----- 4. Register it -----
    components.add(c);
    return true;
}



public boolean detachComponent(Component c) {
    if (c == null) return false;
    if (!components.contains(c)) return false;

   
    // 1. Remove wires connected to this component
    removeWiresConnectedToComponent(c);

    // 2. Clear occupied cells
      for (int cx = c.getX(); cx < c.getX() + c.width; cx++) {
            for (int cy = c.getY(); cy < c.getY() + c.height; cy++) {
             
                this.getCell(cx, cy).setComponent(null);
                
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

public boolean removeComponent(Component c) 
{
    if(!removeComponent(c)) return false;

    c.removeView();
    
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
    pin.clear();
}

private void removeWiresConnectedToComponent(Component c) {

    

    List<Wire> toRemove = new ArrayList<>();

    for(Pin p:c.outputPins)
    {
        
        for(Wire w:p.getConnections())
        {
         
            toRemove.add(w);
        }
    }
    // Disconnect component from sink wires (but keep the wires)
for (Pin input : c.getInputPins())
{

    for (Wire w : new ArrayList<>(input.getConnections()))
    {
        
        w.getSinks().remove(input);  // remove this sink pin
                    // optional
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

    // 1. Clear each edge region from the grid
    for (WireEdge edge : w.getEdges()) {
        int x1 = edge.getA().getX();
        int y1 = edge.getA().getY();
        int x2 = edge.getB().getX();
        int y2 = edge.getB().getY();

        // Horizontal line
        if (y1 == y2) {
            int min = Math.min(x1, x2);
            int max = Math.max(x1, x2);
            for (int x = min; x <= max; x++) {
                Cell cell = getCell(x, y1);
                if (cell != null) cell.removeWire(w);
            }
        }

        // Vertical line
        else if (x1 == x2) {
            int min = Math.min(y1, y2);
            int max = Math.max(y1, y2);
            for (int y = min; y <= max; y++) {
                Cell cell = getCell(x1, y);
                if (cell != null) cell.removeWire(w);
            }
        }
    }

    // 2. Remove wire entirely from circuit list
    wires.remove(w);
    w.removeView();
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
