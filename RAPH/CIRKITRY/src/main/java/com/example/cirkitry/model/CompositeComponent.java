package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;

public class CompositeComponent extends Component {

    // -------------------------------
    // Internal wires for subcomponents
    // -------------------------------
    protected final List<InternalWire> internalWires = new ArrayList<>();

    public CompositeComponent(String name) {
        super("Composite : "+name);
        
        this.type = name;

        this.width=5;
        this.height = 3;
    }

    // -------------------------------
    // Subcomponent management
    // -------------------------------
    @Override
    public void addSubcomponent(Component comp) {
        subcomponents.add(comp);
    }

    public List<InternalWire> getInternalWires() {
        return internalWires;
    }

    // -------------------------------
    // Internal connection
    // -------------------------------
    public void connect(Pin src, Pin dst) {
        InternalWire wire = new InternalWire(src, dst);
        internalWires.add(wire);
    }

    // -------------------------------
    // Compute
    // -------------------------------
    @Override
    public void compute() {
        // 1. Compute all subcomponents first
        for (Component comp : subcomponents) {
            comp.compute();
        }

        // 2. Propagate all internal wires
        for (InternalWire wire : internalWires) {
            wire.propagate();
        }
    }
}
