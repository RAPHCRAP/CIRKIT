package com.example.cirkitry.controller;

import javafx.scene.layout.VBox;
import java.util.Map;
import javafx.scene.control.Button;
import  java.util.HashMap;
import java.util.function.Consumer;
import com.example.cirkitry.model.ComponentFactory;

public class ComponentPaletteController {

    private final VBox paletteRoot;      // UI container where buttons go
    private final Map<String, Button> buttonMap = new HashMap<>();

    public ComponentPaletteController(VBox paletteRoot) {
        this.paletteRoot = paletteRoot;
        refreshPalette();
    }

    /**
     * Rebuilds all buttons based on ComponentFactory registry.
     */
    public void refreshPalette() {
        paletteRoot.getChildren().clear();
        buttonMap.clear();

        for (String typeName : ComponentFactory.getRegisteredTypes()) {
            Button b = createButton(typeName);
            buttonMap.put(typeName, b);
            paletteRoot.getChildren().add(b);
        }
    }

    private Button createButton(String typeName) {
        Button b = new Button(typeName);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    /**
     * Attach an action to ALL buttons: 
     * controller.onComponentSelected("AND") etc.
     */
    public void setOnComponentSelected(Consumer<String> handler) {
        for (Map.Entry<String, Button> e : buttonMap.entrySet()) {
            String type = e.getKey();
            Button btn = e.getValue();
            btn.setOnAction(a -> handler.accept(type));
        }
    }
}
