package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public class ComponentDefinition {

    public String name;

    // Input / output pins are identified by **index** instead of name
    public int inputCount;
    public int outputCount;

    // Subcomponents inside this composite
    public List<SubcomponentDef> subcomponents = new ArrayList<>();

    // Internal connections between pins (srcIndex -> dstIndex)
    public List<ConnectionDef> connections = new ArrayList<>();

    public ComponentDefinition(String name, int inputCount, int outputCount) {
        this.name = name;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }


    public static class SubcomponentDef {
        public String type; // class name of the subcomponent
        public String id;   // unique identifier inside composite

        public SubcomponentDef(String type, String id) {
            this.type = type;
            this.id = id;
        }
    }

    public static class ConnectionDef {
        public PinRef src;
        public PinRef dst;

        public ConnectionDef(PinRef src, PinRef dst) {
            this.src = src;
            this.dst = dst;
        }
    }

    public static class PinRef {
        public String componentId; // "input", "output", or subcomponent id
        public int pinIndex;       // index of the pin in that component

        public PinRef(String componentId, int pinIndex) {
            this.componentId = componentId;
            this.pinIndex = pinIndex;
        }
    }
}
