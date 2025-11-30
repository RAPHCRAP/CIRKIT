package com.example.cirkitry.wmodel;

import javafx.scene.Node;

public class SelectionManager {

    private static SelectableView selected = null;

    public static void makeSelectable(SelectableView selectable) {
        Node node = (Node) selectable;

        node.setOnMouseClicked(event -> {
            if (selected != null) {
                selected.onDeselect();
            }

            selected = selectable;
            selected.onSelect();

            event.consume();
        });
    }

    public static SelectableView getSelected() {
        return selected;
    }

    public static Object getSelectedModel() {
        return selected != null ? selected.getModel() : null;
    }

    public static void deselectAll() {
        if (selected != null) {
            selected.onDeselect();
            selected = null;
        }
    }
}
