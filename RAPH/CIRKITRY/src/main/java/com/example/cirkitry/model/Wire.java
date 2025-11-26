package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;

public class Wire {

    private final Pin source;
    private final List<Pin> sinks = new ArrayList<>();

    private final List<WireNode> nodes = new ArrayList<>();
    private final List<WireEdge> edges = new ArrayList<>();

    private final List<Cell> occupiedCells = new ArrayList<>();

    public Wire(int x, int y, Pin source) {
        if (!source.isOutput())
            throw new IllegalArgumentException("Wire source must be OUTPUT");

        WireNode root = new WireNode(x, y, this);
        nodes.add(root);

        this.source = source;
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
        if (a.getDegree() >= 4 || b.getDegree() >= 4)
            return false;

        WireEdge e = new WireEdge(a, b);
        edges.add(e);

        a.incrementDegree();
        b.incrementDegree();

        return true;
    }

    // --------------------------
    // Remove a specific edge
    // --------------------------
    private void removeEdge(WireEdge e) {
        edges.remove(e);

        e.getA().decrementDegree();
        e.getB().decrementDegree();
    }

    // --------------------------
    // EXTEND EDGE
    // --------------------------
    public boolean extendEdge(WireNode n, int x2, int y2) {

        if (!nodes.contains(n)) return false;

        int x1 = n.getX();
        int y1 = n.getY();

        // CASE 1: Straight
        if (x1 == x2 || y1 == y2) {
            WireNode end = ensureNode(x2, y2);
            return addEdge(n, end);
        }

        // CASE 2: L-shaped
        WireNode mid = ensureNode(x2, y1);
        WireNode end = ensureNode(x2, y2);

        // Check degree constraints BEFORE modifying anything
        if (n.getDegree() >= 4) return false;
        if (mid.getDegree() >= 4) return false;
        if (end.getDegree() >= 4) return false;

        addEdge(n, mid);
        addEdge(mid, end);

        return true;
    }

    // --------------------------
    // DELETE NODE (only if degree = 1)
    // --------------------------
    public boolean deleteNode(WireNode n) {
        if (!nodes.contains(n)) return false;

        if (n.getDegree() != 1)
            return false; // cannot delete a branching or middle node

        // Find the single attached edge
        WireEdge attached = null;
        for (WireEdge e : edges) {
            if (e.getA() == n || e.getB() == n) {
                attached = e;
                break;
            }
        }

        if (attached != null)
            removeEdge(attached);

        nodes.remove(n);
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
    updateOccupiedCells(pathCells);

    return true;
}


private void updateOccupiedCells(List<Cell> newCells) {
    // 1. Remove old wire occupancy
    for (Cell old : occupiedCells) {
        old.removeWire(this);
    }

    occupiedCells.clear();

    // 2. Add new occupancy
    for (Cell c : newCells) {
        c.addWire(this);
        occupiedCells.add(c);
    }
}



    public Pin getSource() 
    {
         return source; 

    } 
    public List<Pin> getSinks() 
    { 
        return sinks; 

    }

}
