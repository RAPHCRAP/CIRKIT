package com.example.cirkitry.business;

import  java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.example.cirkitry.builder.CircuitBuilder;
import com.example.cirkitry.builder.CircuitDefinition;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.ComponentDefinition;
import  com.example.cirkitry.model.ComponentFactory;

public class CircuitFileService {

    private Path currentFile;

    public Circuit load(Path file) throws IOException {

        String json = Files.readString(file);
        CircuitFile fileObj = CircuitSerializer.deserializeFile(json);

        // STEP 1: Register component definitions
        for (ComponentDefinition def : fileObj.componentDefs) {
            ComponentFactory.registerCustomType(def.name, def);
        }

        // STEP 2: Instantiate circuit
        Circuit circuit = CircuitBuilder.instantiate(fileObj.circuitDef);

        this.currentFile = file;
        return circuit;
    }

    public void save(Path file, Circuit circuit) throws IOException {

        // STEP 1: extract custom component definitions
        List<ComponentDefinition> defs = ComponentFactory.getAllCustomDefs();

        // STEP 2: build circuit definition
        CircuitDefinition cdef = CircuitBuilder.toDefinition(circuit);

        // STEP 3: wrap inside file object
        CircuitFile fileObj = new CircuitFile(defs, cdef);

        // STEP 4: serialize
        String json = CircuitSerializer.serializeFile(fileObj);

        Files.writeString(file, json);

        this.currentFile = file;
    }

    public void saveExisting(Circuit circuit) throws IOException {
        if (currentFile == null)
            throw new IllegalStateException("No file has been saved before");
        save(currentFile, circuit);
    }

    public void setCurrentFile(Path file) { this.currentFile = file; }
    public Path getCurrentFile() { return currentFile; }
}
