package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentBuilder {

    private final CompositeComponent component;

    private final Map<String, Pin> inputPins = new HashMap<>();
    private final Map<String, Pin> outputPins = new HashMap<>();

    public ComponentBuilder(String name) {
        component = new CompositeComponent(name);
    }

    // -----------------------
    // Add external pins
    // -----------------------
    public Pin addInput(String name) {
        Pin pin = component.addInputPin(name);
        inputPins.put(name, pin);
        return pin;
    }

    public Pin addOutput(String name) {
        Pin pin = component.addOutputPin(name);
        outputPins.put(name, pin);
        return pin;
    }

    // -----------------------
    // Add internal subcomponents
    // -----------------------
    public <T extends Component> T addSubcomponent(T subcomponent) {
        component.addSubcomponent(subcomponent);
        return subcomponent;
    }

    // -----------------------
    // Connect pins
    // -----------------------
    public void connect(Pin src, Pin dst) {
        component.connect(src, dst);
    }

    // -----------------------
    // Build final component
    // -----------------------
    public CompositeComponent build() {
        return component;
    }

    public ComponentDefinition buildDefinition() {
    int inputCount = component.getInputPins().size();
    int outputCount = component.getOutputPins().size();

    ComponentDefinition def = new ComponentDefinition(component.getName(), inputCount, outputCount);

    // Subcomponents
    for (Component sub : component.getSubcomponents()) {
        def.subcomponents.add(
            new ComponentDefinition.SubcomponentDef(sub.getClass().getSimpleName(), sub.getName())
        );
    }

    // Internal wires
    for (InternalWire w : component.getInternalWires()) {
        String srcCompId = w.getSrc().getParent() == component ? "input" : w.getSrc().getParent().getName();
        String dstCompId = w.getDst().getParent() == component ? "output" : w.getDst().getParent().getName();

        int srcIndex = w.getSrc().getParent() == component
                ? component.getInputPins().indexOf(w.getSrc())
                : w.getSrc().getParent().getOutputPins().indexOf(w.getSrc());

        int dstIndex = w.getDst().getParent() == component
                ? component.getOutputPins().indexOf(w.getDst())
                : w.getDst().getParent().getInputPins().indexOf(w.getDst());

        def.connections.add(new ComponentDefinition.ConnectionDef(
                new ComponentDefinition.PinRef(srcCompId, srcIndex),
                new ComponentDefinition.PinRef(dstCompId, dstIndex)
        ));
    }

    return def;
}


public static CompositeComponent instantiate(ComponentDefinition def) {

        ComponentBuilder builder = new ComponentBuilder(def.name);

        // 1. Create external pins
        List<Pin> inputPins = new ArrayList<>();
        for (int i = 0; i < def.inputCount; i++)
            inputPins.add(builder.addInput("")); // nameless pins

        List<Pin> outputPins = new ArrayList<>();
        for (int i = 0; i < def.outputCount; i++)
            outputPins.add(builder.addOutput(""));

        // 2. Create subcomponents
        Map<String, Component> subMap = new HashMap<>();
        for (ComponentDefinition.SubcomponentDef sub : def.subcomponents) {
            Component c = ComponentFactory.create(sub.type);
            subMap.put(sub.id, builder.addSubcomponent(c));
        }

        // 3. Recreate internal connections
        for (ComponentDefinition.ConnectionDef conn : def.connections) {
            Pin srcPin = getPinByRef(conn.src, inputPins, outputPins, subMap);
            Pin dstPin = getPinByRef(conn.dst, inputPins, outputPins, subMap);
            builder.connect(srcPin, dstPin);
        }

        return builder.build();
    }

    // -----------------------------
    // Helper method: resolve PinRef to Pin instance
    // -----------------------------
    private static Pin getPinByRef(ComponentDefinition.PinRef ref,
                                   List<Pin> inputs,
                                   List<Pin> outputs,
                                   Map<String, Component> subMap) {

        if (ref.componentId.equals("input"))
            return inputs.get(ref.pinIndex);
        if (ref.componentId.equals("output"))
            return outputs.get(ref.pinIndex);

        Component sub = subMap.get(ref.componentId);
        if (sub == null)
            throw new RuntimeException("Unknown subcomponent id: " + ref.componentId);

        // Determine if it is input or output pin
        if (ref.pinIndex < sub.getInputPins().size())
            return sub.getInputPins().get(ref.pinIndex);
        else
            return sub.getOutputPins().get(ref.pinIndex - sub.getInputPins().size());
    }

    // -----------------------
    // Optional getters
    // -----------------------
    public Pin getInput(String name) {
        return inputPins.get(name);
    }

    public Pin getOutput(String name) {
        return outputPins.get(name);
    }
}


// ComponentBuilder builder = new ComponentBuilder("CustomAND");

// Pin A = builder.addInput("A");
// Pin B = builder.addInput("B");
// Pin C = builder.addOutput("C");

// // Add primitive AND gate internally
// AndGate core = builder.addSubcomponent(new AndGate());

// // Connect external pins to internal pins
// builder.connect(A, core.getInA());
// builder.connect(B, core.getInB());
// builder.connect(core.getOutC(), C);

// // Build the final composite component
// CompositeComponent customAnd = builder.build();
