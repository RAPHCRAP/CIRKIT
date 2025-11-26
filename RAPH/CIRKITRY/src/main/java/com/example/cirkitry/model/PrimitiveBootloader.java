package com.example.cirkitry.model;

import com.example.cirkitry.model.primitivegates.AndGate;
import com.example.cirkitry.model.primitivegates.NandGate;
import com.example.cirkitry.model.primitivegates.NorGate;
import com.example.cirkitry.model.primitivegates.NotGate;
import com.example.cirkitry.model.primitivegates.OrGate;
import com.example.cirkitry.model.primitivegates.XnorGate;
import com.example.cirkitry.model.primitivegates.XorGate;

public class PrimitiveBootloader {

    public static void registerAll() {

        ComponentFactory.registerPrimitive("AND", AndGate::new);
        ComponentFactory.registerPrimitive("OR", OrGate::new);
        ComponentFactory.registerPrimitive("NOT", NotGate::new);
        ComponentFactory.registerPrimitive("NAND", NandGate::new);
        ComponentFactory.registerPrimitive("NOR", NorGate::new);
        ComponentFactory.registerPrimitive("XOR", XorGate::new);
        ComponentFactory.registerPrimitive("XNOR", XnorGate::new);
        ComponentFactory.registerPrimitive("NOT", NotGate::new);
        ComponentFactory.registerPrimitive("NAND", NandGate::new);
        ComponentFactory.registerPrimitive("NOR", NorGate::new);
        ComponentFactory.registerPrimitive("XOR", XorGate::new);
        ComponentFactory.registerPrimitive("XNOR", XnorGate::new);

        // PrimitiveRegistry.register("CLOCK", Clock::new);
        // PrimitiveRegistry.register("LED", Led::new);
        // PrimitiveRegistry.register("SWITCH", Switch::new);

        // Add more primitives here...
    }
}