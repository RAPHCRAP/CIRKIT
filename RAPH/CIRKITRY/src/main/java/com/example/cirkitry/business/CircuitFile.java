package com.example.cirkitry.business;

import java.util.ArrayList;
import  java.util.List;

import com.example.cirkitry.builder.CircuitDefinition;
import com.example.cirkitry.model.ComponentDefinition;

public class CircuitFile {

    public List<ComponentDefinition> componentDefs = new ArrayList<>();
    public CircuitDefinition circuitDef;

    public CircuitFile() {}

    public CircuitFile(List<ComponentDefinition> defs, CircuitDefinition circuitDef) {
        this.componentDefs = defs;
        this.circuitDef = circuitDef;
    }
}
