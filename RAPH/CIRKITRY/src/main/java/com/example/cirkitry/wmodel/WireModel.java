package com.example.cirkitry.wmodel;

import com.example.cirkitry.mathsutil.MathUtils;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.WireEdge;
import com.example.cirkitry.model.WireNode;
import com.example.cirkitry.scale.Scale;

import javafx.scene.Group;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class WireModel extends Group 
{

    private String id;
    private Wire wire;
    private double scale=0;
    

    public WireModel(String id,Wire w)
    {
        this.id = id;
        this.wire = w;

        init();
    }

    private void init()
    {

        scale = Scale.WCellScale;
        for(WireNode node: wire.getNodes())
        {
            int x = node.getX();
            int y = node.getY();


            double w=MathUtils.percentageValue(70, scale);
            double h = MathUtils.percentageValue(70 ,scale);
            double d = scale;

            Box n =new Box(w,h,d);

            Pos.setRecPosition(x, y, 1, 1, n);

            this.getChildren().add(n);

        }

        for(WireEdge edge:wire.getEdges())
        {
            int x1 = edge.getA().getX();
            int y1 = edge.getA().getY();

            int x2 = edge.getB().getX();
            int y2= edge.getB().getY();


            Group e = createEdge(x1,y1,x2,y2);
            
            this.getChildren().add(e);
        }

      
    }

      private Group createEdge(int x1, int y1, int x2, int y2)
{
    Group g = new Group();


    
    // Grid delta
    int dx = x2 - x1;
    int dy = y2 - y1;

    // How many cells long?
    int cells = Math.abs(dx) + Math.abs(dy)+ 1;

    double radius = scale * 0.2;             // 20% of scale, thickness of wire
    double height = cells * scale;           // world length

    Cylinder cyl = new Cylinder(radius, height);

    // Rotation: Cylinder is vertical by default
    if (dy == 0) { 
        // Horizontal wire → rotate around Z-axis
        cyl.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
    }

    // Find left-most / top-most cell
    int startX = Math.min(x1, x2);
    int startY = Math.min(y1, y2);

// Position cylinder at midpoint using your helper
if (dy == 0) {
    // Horizontal wire
    Pos.setRecPosition(startX, startY, cells, 1, cyl);
} 
else {
    // Vertical wire
    Pos.setRecPosition(startX, startY, 1, cells, cyl);
}

    g.getChildren().add(cyl);
    return g;
}

}
