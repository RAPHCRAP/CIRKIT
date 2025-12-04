package com.example.cirkitry.graphic;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape3D;

public class applyAction 
{
    public static void applyToAllShapes(Group group, java.util.function.Consumer<Shape3D> action) 
{
    
    for (Node node : group.getChildren()) {
        if (node instanceof Shape3D s) {
            action.accept(s);
        } 
        else if (node instanceof Group g) {
            applyToAllShapes(g, action); // recursive for nested groups
        }
    }
}
}
