package com.example.cirkitry.model;

public class PrimitiveBootloader {

    public static void registerAll() {

        ComponentFactory.registerPrimitive("AND", AndGate::new);
        // PrimitiveRegistry.register("OR", OrGate::new);
        // PrimitiveRegistry.register("NOT", NotGate::new);
        // PrimitiveRegistry.register("NAND", NandGate::new);
        // PrimitiveRegistry.register("XOR", XorGate::new);

        // PrimitiveRegistry.register("CLOCK", Clock::new);
        // PrimitiveRegistry.register("LED", Led::new);
        // PrimitiveRegistry.register("SWITCH", Switch::new);

        // Add more primitives here...
    }
}