package com.example.cirkitry.controller;

import com.example.cirkitry.model.ComponentBuilder;
import com.example.cirkitry.model.ComponentDefinition;
import com.example.cirkitry.model.ComponentFactory;

public class CompositeComponentController {

  
    public CompositeComponentController() {}

    /**
     * Registers a composite component built from the user’s selection.
     */
    public void registerCompositeComponent(String typeName, ComponentBuilder builder) {

        // Validation: prevent duplicates
        if (ComponentFactory.getRegisteredTypes().contains(typeName)) {
            throw new IllegalArgumentException("Component type already exists: " + typeName);
        }

        // Build ComponentDefinition
        ComponentDefinition def = builder.buildDefinition();

        // Register into factory (will trigger UI update automatically)
        ComponentFactory.registerCustomType(typeName, def);

        System.out.println("Composite '" + typeName + "' registered.");
    }
}
