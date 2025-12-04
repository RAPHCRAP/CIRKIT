package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ComponentFactory {

    // Registry of all component types (primitive + custom)
    private static final Map<String, Supplier<Component>> registry = new HashMap<>();



    
    private static final List<Runnable> listeners = new ArrayList<>();

public static void addRegistryListener(Runnable r) {
    listeners.add(r);
}

private static void notifyListeners() {
    for (Runnable r : listeners) r.run();
}



    // -----------------------------
    // Register a primitive type
    // -----------------------------
    public static void registerPrimitive(String typeName, Supplier<Component> factory) {
        registry.put(typeName, factory);
        
        notifyListeners();
    }

    // -----------------------------
    // Register a user-defined composite type
    // -----------------------------
private static final Map<String, ComponentDefinition> customDefinitions = new HashMap<>();

public static void registerCustomType(String typeName, ComponentDefinition def) {
    customDefinitions.put(typeName, def);
    registry.put(typeName, () -> ComponentBuilder.instantiate(def));
    notifyListeners();
}

public static List<ComponentDefinition> getAllCustomDefs() {
    return new ArrayList<>(customDefinitions.values());
}

    // -----------------------------
    // Create a new component by type name
    // -----------------------------
    public static Component create(String typeName) {

        
        Supplier<Component> factory = registry.get(typeName);
        if (factory == null)
            throw new IllegalArgumentException("Unknown component type: " + typeName);
        return factory.get();
    }

    public static Set<String> getRegisteredTypes() {
    return registry.keySet();
}

}

// START OF THE APP RESGISTER PRIMITIVES

// // Build a custom component and save recipe
// ComponentBuilder builder = new ComponentBuilder("CustomAND");
// Pin a = builder.addInput(""); 
// Pin b = builder.addInput("");
// Pin c = builder.addOutput("");

// AndGate core = builder.addSubcomponent(new AndGate());
// builder.connect(a, core.getInputPins().get(0));
// builder.connect(b, core.getInputPins().get(1));
// builder.connect(core.getOutputPins().get(0), c);

// CompositeComponent comp = builder.build();

// // Save recipe
// ComponentDefinition def = builder.buildDefinition();
// ComponentFactory.registerCustomType("CustomAND", def);

// // Later, instantiate from definition
// CompositeComponent newInstance = ComponentBuilder.instantiate(def);