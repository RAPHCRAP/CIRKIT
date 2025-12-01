package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    private final int x;
    private final int y;

    // What component currently occupies this cell (if any)
    private Component component;

    // If the cell holds a pin belonging to that component
    private Pin pin;

    // Wires that pass through this cell (including bends)
    private final List<Wire> wires = new ArrayList<>();

private WireNode node;   // only one node allowed



    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // ---------------------
    // Cell coordinate API
    // ---------------------

    public int getX() { return x; }
    public int getY() { return y; }

    // ---------------------
    // Component occupancy
    // ---------------------

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component comp) {
        this.component = comp;
    }

    public boolean isEmpty() {
        return component == null && pin == null && wires.isEmpty();
    }

    // ---------------------
    // Pin attachment
    // ---------------------

    public Pin getPin() {
        return pin;
    }

    public void setPin(Pin pin) {
        this.pin = pin;
    }

    public boolean hasPin() {
        return pin != null;
    }

    // ---------------------
    // Wire occupancy
    // ---------------------

    public void addWire(Wire wire) {
        if (!wires.contains(wire)) {
            wires.add(wire);
        }
    }

    public void removeWire(Wire wire) {

         // If the cell contains this wire's node → clear it
    if (getNode() != null && getNode().getWire() == wire) 
        {
            clearNode();
        }
        wires.remove(wire);
    }


    public List<Wire> getWires() {
        return wires;
    }

    public boolean hasWire() {
        return !wires.isEmpty();
    }
public void setNode(WireNode node) {
    this.node = node;
}

public void clearNode() {
    this.node = null;
}

public WireNode getNode() {
    return node;
}

public boolean hasNode() {
    return node != null;
}


    // ---------------------
    // Utility / conflict checks
    // ---------------------

   public boolean canMoveComponent(Component comp) {

    // -------- 1. Component on this cell --------
    if (component != null && component != comp) {
        // Another component occupies this cell → cannot move here
        return false;
    }

    // -------- 2. Pin on this cell --------
    if (pin != null) {
        // Allow ONLY if this pin belongs to the moving component
        if (pin.getParent() != comp) {
            return false;
        }
    }

    // -------- 3. Wires on this cell --------
    for (Wire w : wires) {

        // Case A: wire belongs to this component as source → allowed
        if (w.getSource().getParent() == comp) {
            continue;
        }

        // Case B: wire belongs to another component → block
        return false;
    }

    // -------- 4. Node on this cell --------
    if (node != null && node.getWire().getSource().getParent() != comp) {
        return false;
    }

    // All checks passed
    return true;
}

    public boolean canPlaceComponent() {
    return component == null && pin == null && wires.isEmpty() && node == null;
     }

public boolean canPlaceWire(Wire wire) {

    // ===== COMPONENT BLOCK =====
    if (component != null && pin == null)
        return false;


    // ===== PIN LOGIC =====
    if (pin != null) 
    {

// CASE 1: This pin is the wire's source pin (OUTPUT)
        if (pin == wire.getSource()) 
        {
    // Only allow if no nodes exist yet (wire not placed)
            if (wire.getNodes().isEmpty() || wire.getNodes().get(0).getX() != this.x || wire.getNodes().get(0).getY() != this.y) 
            {
                return true;
            } else 
            {
        // Wire already has its first node here → cannot place again
                return false;
            }
        }


        // CASE 2: Output pin but not the wire's source → cannot place
        if (pin.isOutput())
            return false;

        // CASE 3: Input pin (valid attach point)
        if (pin.isInput()) {

            // Already connected to this wire → fine
            if (pin.getConnections().contains(wire))
                return true;

            // If input pin has another wire and you only allow single-source:
            if (!pin.getConnections().isEmpty())
                return false;

            // Can attach to this input pin
            return true;
        }
    }

    // ===== WIRE NODE LOGIC =====
    // If a different wire already owns this node, block
    if (node != null && node.getWire() != wire)
        return false;

    // Allow crossing wires + allow reusing your own cells
    return true;
}

public boolean hasComponent()
{
    return component != null;
}







}
