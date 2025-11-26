package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pin 
{

    private int relX, relY;
    private final PinType type;
    private final Component parent;

    // current stable signal (what the component reads)
    private boolean signal = false;

    // value written during a tick, applied later by updateSignal()
    private boolean nextSignal = false;

    // wires connected to this pin
    private final List<Wire> connections = new ArrayList<>();

    public Pin(PinType type, Component parent) {
        this.type = Objects.requireNonNull(type);
        this.parent = parent;
    }




    // Queries
    public boolean getSignal() {
        return signal;
    }

    public PinType getType() {
        return type;
    }

    public Component getParent() {
        return parent;
    }

    public boolean isInput() {
        return type == PinType.INPUT;
    }

    public boolean isOutput() {
        return type == PinType.OUTPUT;
    }

    // Called by a Wire during propagation to write the next value
    public void setNextSignal(boolean s) {
        nextSignal = s;
    }

    // Called at the end of the tick to make nextSignal the stable value
    public void updateSignal() {
        signal = nextSignal;
    }

    // Connection management
    public void addConnection(Wire w) {
        if (!connections.contains(w)) connections.add(w);
    }

    public void removeConnection(Wire w) {
        connections.remove(w);
    }

    public List<Wire> getConnections() {
        return connections;
    }


    public int getAbsoluteX() {
        return parent.x + relX;
    }

    public int getAbsoluteY() {
        return parent.y + relY;
    }

    public void setRelative(int x, int y) {
        this.relX = x;
        this.relY = y;
    }
}