package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    if(!detachComponent(c)) return false;

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
    // COMPOSITE COMP GENERATION
    // ------------------------------------------------------------





/**
 * Extracts a subcircuit inside the inclusive rectangle (x1,y1)-(x2,y2),
 * builds a CompositeComponent from it, and replaces the region with the new composite.
 *
 * Returns the created CompositeComponent on success, or throws an exception on failure.
 */
public ComponentBuilder extractCompositeFromRect(int x1, int y1, int x2, int y2, String compositeTypeName) {


    int rx1 = Math.min(x1, x2), ry1 = Math.min(y1, y2);
    int rx2 = Math.max(x1, x2), ry2 = Math.max(y1, y2);

    // collect inside components
    List<Component> inside = new ArrayList<>();
    for (Component c : new ArrayList<>(components)) {
        if (componentInsideRect(c, rx1, ry1, rx2, ry2)) {
            inside.add(c);
        }
    }

    if (inside.isEmpty()) {
        return null;
    }

    // ----------------------------------------------------
    // STEP A: Convert real wires → SimpleWire edges
    // ----------------------------------------------------
    List<SimpleWire> edges = new ArrayList<>();

    for (Wire w : wires) {
        for (Pin sink : w.getSinks()) {
            edges.add(new SimpleWire(w.getSource(), sink, w));
        }
    }

    // ----------------------------------------------------
    // STEP B: Classify edges
    // ----------------------------------------------------
    List<SimpleWire> internalEdges = new ArrayList<>();
    List<SimpleWire> enteringEdges  = new ArrayList<>();
    List<SimpleWire> leavingEdges   = new ArrayList<>();

    for (SimpleWire e : edges) {
        Component srcComp = e.src.getParent();
        Component dstComp = e.dst.getParent();

        boolean srcInside = inside.contains(srcComp);
        boolean dstInside = inside.contains(dstComp);

        if (srcInside && dstInside) {
            internalEdges.add(e);
        } else if (!srcInside && dstInside) {
            enteringEdges.add(e);
        } else if (srcInside && !dstInside) {
            leavingEdges.add(e);
        }
    }


    // if(leavingEdges.isEmpty()&&enteringEdges.isEmpty()) 
    // {
    //     return null;
    // }
    // ----------------------------------------------------
    // STEP C: Cycle check using SimpleWire internalEdges
    // ----------------------------------------------------
    if (!isAcyclic(inside, internalEdges)) {
        return null;
    }

    // ----------------------------------------------------
    // STEP D: Build composite
    // ----------------------------------------------------
    ComponentBuilder builder = new ComponentBuilder(compositeTypeName);

    Map<Component, Component> cloneMap = new HashMap<>();
    for (Component orig : inside) {
        Component clone = ComponentFactory.create(orig.getType());
        builder.addSubcomponent(clone);
        cloneMap.put(orig, clone);
    }

    // ----------------------------------------------------
    // STEP E: Clone all pins
    // ----------------------------------------------------
    Map<Pin, Pin> pinCloneMap = new HashMap<>();
    for (Component orig : inside) {
        Component clone = cloneMap.get(orig);

        List<Pin> origIns = orig.getInputPins();
        List<Pin> cloneIns = clone.getInputPins();
        for (int i = 0; i < origIns.size() && i < cloneIns.size(); i++) {
            pinCloneMap.put(origIns.get(i), cloneIns.get(i));
        }

        List<Pin> origOuts = orig.getOutputPins();
        List<Pin> cloneOuts = clone.getOutputPins();
        for (int i = 0; i < origOuts.size() && i < cloneOuts.size(); i++) {
            pinCloneMap.put(origOuts.get(i), cloneOuts.get(i));
        }
    }

    // ----------------------------------------------------
    // STEP F: External IO mapping
    // ----------------------------------------------------
    Map<Pin, Pin> internalDstToExternalInput  = new HashMap<>();
    Map<Pin, Pin> internalSrcToExternalOutput = new HashMap<>();
    int inCounter = 0, outCounter = 0;

    // ----------------------------------------------------
    // STEP G: Rebuild internal wiring
    // ----------------------------------------------------
    for (SimpleWire e : internalEdges) {
        Pin clonedSrc = pinCloneMap.get(e.src);
        Pin clonedDst = pinCloneMap.get(e.dst);
        if (clonedSrc != null && clonedDst != null) {
            builder.connect(clonedSrc, clonedDst);
        }
    }

    // ----------------------------------------------------
    // STEP H: entering wires (outside → inside)
    // ----------------------------------------------------
    for (SimpleWire e : enteringEdges) {
        Pin origDst = e.dst;

        Pin ext = internalDstToExternalInput.get(origDst);
        if (ext == null) {
            ext = builder.addInput("IN" + (inCounter++));
            internalDstToExternalInput.put(origDst, ext);
        }

        Pin clonedDst = pinCloneMap.get(origDst);
        if (clonedDst != null) {
            builder.connect(ext, clonedDst);
        }
    }

    // ----------------------------------------------------
    // STEP I: leaving wires (inside → outside)
    // ----------------------------------------------------
    for (SimpleWire e : leavingEdges) {
        Pin origSrc = e.src;

        Pin ext = internalSrcToExternalOutput.get(origSrc);
        if (ext == null) {
            ext = builder.addOutput("OUT" + (outCounter++));
            internalSrcToExternalOutput.put(origSrc, ext);
        }

        Pin clonedSrc = pinCloneMap.get(origSrc);
        if (clonedSrc != null) {
            builder.connect(clonedSrc, ext);
        }
    }

    return builder;
    // // 11) Place composite at rx1, ry1 (top-left)
    // // note: placeInCircuit will apply preferred size/layout/pin placement logic
    // if (!composite.placeInCircuit(rx1, ry1, this)) {
    //     throw new IllegalStateException("Failed to place generated composite at " + rx1 + "," + ry1);
    // }

    // // 12) Remove old wires and components
    // // Remove internal wires and those which we will replace (entering/leaving)
    // Set<Wire> toRemoveWires = new HashSet<>();
    // toRemoveWires.addAll(internalWires);
    // toRemoveWires.addAll(enteringWires);
    // toRemoveWires.addAll(leavingWires);

    // for (Wire w : toRemoveWires) {
    //     // circuit.removeWire should also cleanup pin connections
    //     removeWire(w);
    // }

    // // Remove original components (they will have their pins/wires already cleared by removeWire)
    // for (Component c : inside) {
    //     removeComponent(c);
    // }

    // // 13) Recreate external wires to hook outer world to the new composite pins
    // // For entering wires (outside -> inside): create new wire from original source -> composite external input
    // for (Wire old : enteringWires) {
    //     // for each sink that was inside, we created external pin internalDstToExternalInput.get(origDst)
    //     Pin origSrc = old.getSource();
    //     for (Pin origDst : old.getSinks()) {
    //         if (!internalDstToExternalInput.containsKey(origDst)) continue;
    //         Pin compositeInput = internalDstToExternalInput.get(origDst);

    //         // build a new wire: root at source pin absolute location
    //         int sx = origSrc.getAbsoluteX();
    //         int sy = origSrc.getAbsoluteY();
    //         Wire newW = new Wire(sx, sy, origSrc);
    //         newW.addSink(compositeInput);
    //         addWire(newW);
    //     }
    // }

    // // For leaving wires (inside -> outside): create new wire from composite external output -> original sink
    // for (Wire old : leavingWires) {
    //     Pin origSrc = old.getSource();
    //     Pin compositeOut = internalSrcToExternalOutput.get(origSrc);
    //     if (compositeOut == null) continue;
    //     // attach to each original sink (which was outside)
    //     for (Pin origDst : old.getSinks()) {
    //         if (inside.contains(origDst.getParent())) continue; // only external sinks
    //         // root at composite output pin absolute position
    //         int sx = compositeOut.getAbsoluteX();
    //         int sy = compositeOut.getAbsoluteY();
    //         Wire newW = new Wire(sx, sy, compositeOut);
    //         newW.addSink(origDst);
    //         addWire(newW);
    //     }
    // }

    // // 14) Register composite in main components list (if addComponent didn't do it)
    // if (!components.contains(composite)) {
    //     components.add(composite);
    // }

    // // done
    
}





    // ------------------------------------------------------------
    // COMPOSITE COMP GENERATION : HELPERS
    // ------------------------------------------------------------

private boolean componentInsideRect(Component c, int x1, int y1, int x2, int y2) {
    int cx1 = c.getX(), cy1 = c.getY();
    int cx2 = cx1 + c.getWidth() - 1;
    int cy2 = cy1 + c.getHeight() - 1;
    return cx1 >= x1 && cy1 >= y1 && cx2 <= x2 && cy2 <= y2;
}
private boolean isAcyclic(List<Component> comps, List<SimpleWire> internalEdges) {

    Map<Component, Set<Component>> adj = new HashMap<>();
    Map<Component, Integer> indeg = new HashMap<>();

    for (Component c : comps) {
        adj.put(c, new HashSet<>());
        indeg.put(c, 0);
    }

    for (SimpleWire e : internalEdges) {

        Component src = e.src.getParent();
        Component dst = e.dst.getParent();

        if (!adj.containsKey(src) || !adj.containsKey(dst)) continue;

        if (adj.get(src).add(dst)) {
            indeg.put(dst, indeg.get(dst) + 1);
        }
    }

    Queue<Component> q = new LinkedList<>();
    for (Component c : comps) {
        if (indeg.get(c) == 0) q.add(c);
    }

    int visited = 0;

    while (!q.isEmpty()) {
        Component cur = q.remove();
        visited++;

        for (Component nx : adj.get(cur)) {
            int d = indeg.get(nx) - 1;
            indeg.put(nx, d);
            if (d == 0) q.add(nx);
        }
    }

    return visited == comps.size();
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


    w.getSource().clear();

    for(Pin p:w.getSinks())
    {
        p.clear();
    }
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
                if (cell != null)
                {
                    cell.removeWire(w);
                }
            }
        }

        // Vertical line
        else if (x1 == x2) {
            int min = Math.min(y1, y2);
            int max = Math.max(y1, y2);
            for (int y = min; y <= max; y++) {
                Cell cell = getCell(x1, y);
                if (cell != null)
                {
                    cell.removeWire(w);
                }
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
