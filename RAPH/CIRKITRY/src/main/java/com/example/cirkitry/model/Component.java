package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected List<Pin> inputPins = new ArrayList<>();
    protected List<Pin> outputPins = new ArrayList<>();
    protected List<Cell> occupiedCells = new ArrayList<>();

    // Any component (gate, switch, LED…) will implement this
    public abstract void computeOutput();

    // --- Pin Management --------------------------------------------------

    public List<Pin> getInputPins() {
        return inputPins;
    }

    public List<Pin> getOutputPins() {
        return outputPins;
    }

    public void addInputPin(Pin pin) {
        inputPins.add(pin);
    }

    public void addOutputPin(Pin pin) {
        outputPins.add(pin);
    }

    // --- Grid Occupancy --------------------------------------------------

    public void addOccupiedCell(Cell c) {
        occupiedCells.add(c);
        c.setComponent(this); // Tell the cell who lives here
    }

    public List<Cell> getOccupiedCells() {
        return occupiedCells;
    }
}
