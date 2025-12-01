package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;

import com.example.cirkitry.wmodel.SelectableView;

public class Wire {

    private final Pin source;
    private final List<Pin> sinks = new ArrayList<>();

    private final List<WireNode> nodes = new ArrayList<>();
    private final List<WireEdge> edges = new ArrayList<>();

    private final List<Cell> occupiedCells = new ArrayList<>();

    private SelectableView viewGroup;

    public Wire(int x, int y, Pin source) 
    {
         if (!source.isOutput())
            throw new IllegalArgumentException("Wire source must be OUTPUT");

        
        WireNode root = new WireNode(x, y, this);
        nodes.add(root);

        this.source = source;
        source.addConnection(this);
    }

    
        
    // -------------------------- // Wiring logic // --------------------------
    public void addSink(Pin sink) 
    { 
        if (!sink.isInput()) throw new IllegalArgumentException("Wire sink must be INPUT"); 
        sinks.add(sink); sink.addConnection(this); 
    }
    // --------------------------
    // Node helpers
    // --------------------------
    private WireNode findNode(int x, int y) {
        for (WireNode n : nodes)
            if (n.getX() == x && n.getY() == y) return n;
        return null;
    }

    private WireNode ensureNode(int x, int y) {
        WireNode n = findNode(x, y);
        if (n != null) return n;

        n = new WireNode(x, y, this);
        nodes.add(n);
        return n;
    }

    // --------------------------
    // Add a new edge (with degree limits)
    // --------------------------
   private boolean addEdge(WireNode a, WireNode b) {
    // prevent adding the same edge twice (either orientation)
    for (WireEdge e : edges) {
        if ((e.getA() == a && e.getB() == b) || (e.getA() == b && e.getB() == a)) {
            return false; // already exists
        }
    }

    if (a.getDegree() >= 4 || b.getDegree() >= 4)
        return false;

    WireEdge e = new WireEdge(a, b);
    edges.add(e);

    a.incrementDegree();
    b.incrementDegree();

    return true;
}


public boolean deleteNode(WireNode n, Circuit circuit) {
    if (!nodes.contains(n)) return false;
    if (n.getDegree() != 1) return false; // only endpoints

    // 1. Find the attached edge and neighbor node
    WireEdge attached = null;
    WireNode neighbor = null;
    for (WireEdge e : new ArrayList<>(edges)) {
        if (e.getA() == n) { attached = e; neighbor = e.getB(); break; }
        if (e.getB() == n) { attached = e; neighbor = e.getA(); break; }
    }

    if (attached == null||neighbor==null) return false; // should not happen

    // 2. Remove the edge from wire
    edges.remove(attached);
    n.decrementDegree();
    neighbor.decrementDegree();

    // 3. Release cells along this edge (from n to neighbor, excluding neighbor)
    releaseEdgeCells(n, neighbor, circuit);

    // 4. Remove orphan node n
    nodes.remove(n);
    Cell c = circuit.getCell(n.getX(), n.getY());
    if (c != null && c.getNode() == n) c.clearNode();

    return true;
}

/** Releases the cells along an edge from start -> end, excluding end */
private void releaseEdgeCells(WireNode start, WireNode end, Circuit circuit) {
    int x1 = start.getX(), y1 = start.getY();
    int x2 = end.getX(), y2 = end.getY();

    if (x1 == x2) { // vertical
        int step = y1 < y2 ? 1 : -1;
        for (int y = y1; y != y2; y += step) {
            Cell c = circuit.getCell(x1, y);
            if (c != null) {
                c.removeWire(this);
                occupiedCells.remove(c);
            }
        }
    } else if (y1 == y2) { // horizontal
        int step = x1 < x2 ? 1 : -1;
        for (int x = x1; x != x2; x += step) {
            Cell c = circuit.getCell(x, y1);
            if (c != null) {
                c.removeWire(this);
                occupiedCells.remove(c);
            }
        }
    } else {
        throw new IllegalArgumentException("Edge must be straight");
    }
}

public boolean canExtend(WireNode n, int x2, int y2, Circuit circuit)
{
        if (!nodes.contains(n)) return false;

    int x1 = n.getX();
    int y1 = n.getY();

    List<Cell> proposedCells = new ArrayList<>(occupiedCells);
    if (x1 == x2 || y1 == y2) {
        if (!canPlaceLine(x1, y1, x2, y2, circuit, proposedCells, true)) // skip start
            return false;
    } else {
        // L-shape: n -> mid -> end
        int midX = x2, midY = y1;
        if (!canPlaceInLine(x1, y1, midX, midY, circuit,  true)) return false; // skip n
        if (!canPlaceInLine(midX, midY, x2, y2, circuit,  false)) return false; // mid is new, do NOT skip
    }

    return true;
}

private boolean canPlaceInLine(int x1, int y1, int x2, int y2, Circuit circuit, boolean skipStart) {
    if (x1 == x2) { // vertical
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);
        if (skipStart) start += 1;

        for (int y = start; y <= end; y++) {
            Cell c = circuit.getCell(x1, y);
            if (c == null || !c.canPlaceWire(this)) return false;
         
        }
    } else if (y1 == y2) { // horizontal
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);
        if (skipStart) start += 1;

        for (int x = start; x <= end; x++) {
            Cell c = circuit.getCell(x, y1);
            if (c == null || !c.canPlaceWire(this)) return false;
       
        }
    } else {
        throw new IllegalArgumentException("Line must be horizontal or vertical");
    }
    return true;
}


    // --------------------------
    // EXTEND EDGE
    // --------------------------
public boolean extendEdge(WireNode n, int x2, int y2, Circuit circuit) {
    if (!nodes.contains(n)) return false;



    int x1 = n.getX();
    int y1 = n.getY();

    if(x1==x2&&y1==y2) return false;

    List<Cell> proposedCells = new ArrayList<>(occupiedCells);
    if (x1 == x2 || y1 == y2) {
        if (!canPlaceLine(x1, y1, x2, y2, circuit, proposedCells, true)) // skip start
            return false;
    } else {
        // L-shape: n -> mid -> end
        int midX = x2, midY = y1;
        if (!canPlaceLine(x1, y1, midX, midY, circuit, proposedCells, true)) return false; // skip n
        if (!canPlaceLine(midX, midY, x2, y2, circuit, proposedCells, false)) return false; // mid is new, do NOT skip
    }

    

    // Safe to create nodes & edges
    WireNode end = ensureNode(x2, y2);
    if (x1 == x2 || y1 == y2) {
        addEdge(n, end);
    } else {
        WireNode mid = ensureNode(x2, y1);
        addEdge(n, mid);
        addEdge(mid, end);
    }

    // Update wire occupancy
    updateOccupiedCells(proposedCells, circuit);
    return true;
}


private boolean canPlaceLine(int x1, int y1, int x2, int y2, Circuit circuit, List<Cell> pathCells, boolean skipStart) {
    if (x1 == x2) { // vertical
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);
        if (skipStart) start += 1;

        for (int y = start; y <= end; y++) {
            Cell c = circuit.getCell(x1, y);
            if (c == null || !c.canPlaceWire(this)) return false;
            pathCells.add(c);
        }
    } else if (y1 == y2) { // horizontal
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);
        if (skipStart) start += 1;

        for (int x = start; x <= end; x++) {
            Cell c = circuit.getCell(x, y1);
            if (c == null || !c.canPlaceWire(this)) return false;
            pathCells.add(c);
        }
    } else {
        throw new IllegalArgumentException("Line must be horizontal or vertical");
    }
    return true;
}








    public List<Cell> getOccupiedCells() { 
        return occupiedCells; 
    } 
    // -------------------------- // Signal propagation // -------------------------- 
    public void propagate() 
    { boolean value = source.getSignal(); 
        for (Pin sink : sinks) 
            { 
                sink.setNextSignal(value); 
            } 
    } 
    // -------------------------- // Getters // -------------------------- 
    // 

    public boolean removeSink(Pin sink) {
    if (sink == null) return false;

    boolean removed = sinks.remove(sink);

    if (removed) {
        sink.removeConnection(this);   // <-- so Pin stays consistent
    }

    return removed;
}


private boolean canPlaceWire(List<Cell> pathCells, Circuit circuit) {

    pathCells.clear(); // ensure empty

    for (WireEdge edge : edges) {
        WireNode a = edge.getA();
        WireNode b = edge.getB();

        int x1 = a.getX(), y1 = a.getY();
        int x2 = b.getX(), y2 = b.getY();

        // Horizontal segment
        if (y1 == y2) {
            int start = Math.min(x1, x2);
            int end   = Math.max(x1, x2);

            for (int x = start; x <= end; x++) {
                Cell c = circuit.getCell(x, y1);
                if (!c.canPlaceWire(this))
                    return false;

                pathCells.add(c);
            }
        }

        // Vertical segment
        else if (x1 == x2) {
            int start = Math.min(y1, y2);
            int end   = Math.max(y1, y2);

            for (int y = start; y <= end; y++) {
                Cell c = circuit.getCell(x1, y);
                if (!c.canPlaceWire(this))
                    return false;

                pathCells.add(c);
            }
        }

        // Should never occur; edges are guaranteed straight
        else return false;
    }

    return true;
}

public boolean canPlace(Circuit circuit) {
    return canPlaceWire(new ArrayList<>(), circuit);
}



public boolean placeInCircuit(Circuit circuit) {

    List<Cell> pathCells = new ArrayList<>();

    if (!canPlaceWire(pathCells, circuit))
        return false;

    // Placement valid → commit
    updateOccupiedCells(pathCells,circuit);

    return true;
}



private void updateOccupiedCells(List<Cell> newCells, Circuit circuit) {

    // -------- 1. Clear old occupancy --------
    for (Cell old : occupiedCells) {
        old.removeWire(this);

        if (old.getNode() != null && old.getNode().getWire() == this)
            old.clearNode();
    }
    occupiedCells.clear();


    // -------- 2. Set new occupancy (wires only) --------
    for (Cell c : newCells) {
        c.addWire(this);
        occupiedCells.add(c);
    }


    // -------- 3. Place nodes --------
    for (WireNode node : nodes) {
        Cell c = circuit.getCell(node.getX(), node.getY());
        if (c != null) {
            c.setNode(node);
        }
    }


    // -------- 4. Auto-attach to pins (ONLY after wires & nodes are placed) --------
    for (Cell c : newCells) {
        Pin pin = c.getPin();
        if (pin != null) {

            // Only attach to input pins
            if (pin.isInput() && !pin.getConnections().contains(this)) {
                this.addSink(pin);    // Safe bi-directional add
            }
        }
    }
}



  public List<WireEdge> getEdges()
    {
        return edges;
    }

    public List<WireNode> getNodes()
    {
        return nodes;
    }

    public Pin getSource() 
    {
         return source; 

    } 
    public List<Pin> getSinks() 
    { 
        return sinks; 

    }

    public void setView(SelectableView sv)
    {
        
        this.viewGroup = sv;
        
    }

    public void rebuild()
    {
        System.err.println(this.viewGroup);
        if(viewGroup!=null)
        {
            viewGroup.rebuild();
        }
    }

    public SelectableView getView()
    {
        return viewGroup;
    }

    public void removeView()
    {
        viewGroup.removeFromSubSceneRoot();
    }
}
