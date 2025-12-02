package com.example.cirkitry.handler;


import com.example.cirkitry.scale.Scale;
import com.example.cirkitry.wmodel.Pos;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;

public class Highlight extends Group 
{

    private double scale=0;
    
    

    public Highlight()
    {
        

        init();
    }

    private void init()
    {

        scale = Scale.WCellScale;


     
      
    }

 public void update(int x1, int y1, int x2, int y2) {
    this.getChildren().clear();
    
    // Determine top-left corner
    int topLeftX = Math.min(x1, x2);
    int topLeftY = Math.min(y1, y2);

    // Calculate inclusive width/height (+1)
    int width  = Math.abs(x2 - x1) + 1;
    int height = Math.abs(y2 - y1) + 1;

    // Scale factors
    

    double w = width  * scale;
    double h = height * scale;
    double d = 20; // box thickness

    // Create box
    Box box = new Box(w, h, d);

    // Use top-left as required
    Pos.setRecPosition(topLeftX, topLeftY, width, height, box);

    this.getChildren().add(box);
}

  

    public void setColor(Color color) {
    applyToAllShapes(this, shape -> {
        PhongMaterial mat = new PhongMaterial(color);
        shape.setMaterial(mat);
    });
}

public void setOpacityto(double opacity) {
    applyToAllShapes(this, shape -> {
        shape.setOpacity(opacity);
    });
}

/** Helper: walk through all children & apply property to 3D shapes */
private void applyToAllShapes(Group group, java.util.function.Consumer<Shape3D> action) 
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
