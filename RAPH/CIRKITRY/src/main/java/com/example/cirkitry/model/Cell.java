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
        wires.remove(wire);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public boolean hasWire() {
        return !wires.isEmpty();
    }

    // ---------------------
    // Utility / conflict checks
    // ---------------------

    public boolean canPlaceComponent() {
        // No component here AND no pin AND no wires
        return component == null && pin == null && wires.isEmpty();
    }

    public boolean canPlaceWire() {
        // Wires can overlap other wires + empty space,
        // but cannot overlap pins or components
        return component == null && pin == null;
    }
}
