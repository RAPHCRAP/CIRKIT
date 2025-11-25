package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected String name;

    // Logical structure
    protected final List<Pin> inputPins = new ArrayList<>();
    protected final List<Pin> outputPins = new ArrayList<>();
    protected final List<Component> subcomponents = new ArrayList<>();

    // Physical layout (optional for your grid UI)
    protected final List<Cell> occupiedCells = new ArrayList<>();

    public Component(String name) {
        this.name = name;
    }

    // ------------------------------
    // Pin Management
    // ------------------------------

    public Pin addInputPin(String name) {
        Pin pin = new Pin(PinType.INPUT,this);
        inputPins.add(pin);
        return pin;
    }

    public Pin addOutputPin(String name) {
        Pin pin = new Pin(PinType.OUTPUT,this);
        outputPins.add(pin);
        return pin;
    }

    public List<Pin> getInputPins() {
        return inputPins;
    }

    public List<Pin> getOutputPins() {
        return outputPins;
    }

    // ------------------------------
    // Subcomponent Management (for composites)
    // ------------------------------

    public void addSubcomponent(Component comp) {
        subcomponents.add(comp);
    }

    public List<Component> getSubcomponents() {
        return subcomponents;
    }

    // ------------------------------
    // Layout / Grid
    // ------------------------------

    public void occupyCell(Cell cell) {
        occupiedCells.add(cell);
    }

    public List<Cell> getOccupiedCells() {
        return occupiedCells;
    }

    public void commitPins()
    {
        for (Component sub : subcomponents) sub.commitPins();
    for (Pin p : outputPins) p.updateSignal();
    }

    // ------------------------------
    // Simulation Hook
    // ------------------------------

    /**
     * Every component must implement its logic here.
     * For a primitive gate → do the boolean operation.
     * For a composite component → call compute() on its subcomponents in order.
     */
    public abstract void compute();

    // ------------------------------
    // Name
    // ------------------------------

    public String getName() {
        return name;
    }
}