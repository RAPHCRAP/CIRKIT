package com.example.cirkitry.handler;




import com.example.cirkitry.scale.Scale;
import com.example.cirkitry.wmodel.Pos;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;

public class ComponentGhost extends Group 
{

    private double scale=0;
    
    

    public ComponentGhost()
    {
        

        init();
    }

    private void init()
    {

        scale = Scale.WCellScale;


     
      
    }

  public void update(int x, int y, int width, int height) {
        this.getChildren().clear();


          double w= scale*width;
            double h = scale*height;
            double d = scale;

            Box n =new Box(w,h,d);

            Pos.setRecPosition(x, y, width, height, n);
            this.getChildren().add(n);

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
