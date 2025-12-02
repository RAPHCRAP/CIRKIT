package com.example.cirkitry.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import  com.example.cirkitry.model.ComponentFactory;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ComponentPaletteController {

    private final VBox paletteRoot;
    private final Map<String, Button> buttonMap = new HashMap<>();

    // <— store handler here!
    private Consumer<String> componentSelectHandler = null;

    public ComponentPaletteController(VBox paletteRoot) {
        this.paletteRoot = paletteRoot;
        refreshPalette();
    }

    public void refreshPalette() {
        paletteRoot.getChildren().clear();
        buttonMap.clear();

        for (String typeName : ComponentFactory.getRegisteredTypes()) {
            Button b = createButton(typeName);
            buttonMap.put(typeName, b);
            paletteRoot.getChildren().add(b);

            // <— reapply handler if it exists!
            if (componentSelectHandler != null) {
                b.setOnAction(a -> componentSelectHandler.accept(typeName));
            }
        }
    }

    private Button createButton(String typeName) {
        Button b = new Button(typeName);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    /**
     * Store the handler and apply immediately.
     */
    public void setOnComponentSelected(Consumer<String> handler) {
        this.componentSelectHandler = handler; // store it

        for (Map.Entry<String, Button> e : buttonMap.entrySet()) {
            String type = e.getKey();
            Button btn = e.getValue();
            btn.setOnAction(a -> handler.accept(type));
        }
    }
}
