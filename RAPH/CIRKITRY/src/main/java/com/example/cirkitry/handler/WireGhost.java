package com.example.cirkitry.handler;



import com.example.cirkitry.mathsutil.MathUtils;
import com.example.cirkitry.scale.Scale;
import com.example.cirkitry.wmodel.Pos;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;

public class WireGhost extends Group 
{

    private double scale=0;
    
    

    public WireGhost()
    {
        

        init();
    }

    private void init()
    {

        scale = Scale.WCellScale;


     
      
    }

  public void update(int x1, int y1, int x2, int y2) {
        this.getChildren().clear();


          double w=MathUtils.percentageValue(70, scale);
            double h = MathUtils.percentageValue(70 ,scale);
            double d = scale;

            Box n =new Box(w,h,d);

            Pos.setRecPosition(x1, y1, 1, 1, n);
            this.getChildren().add(n);

            Box e = new Box(w,h,d);

            Pos.setRecPosition(x2, y2, 1, 1, e);
             this.getChildren().add(e);

           

        // Straight line
        if (x1 == x2 || y1 == y2) {
            Group straight = createEdge(x1, y1, x2, y2);
            this.getChildren().add(straight);
            return;
        }

        // L shape: choose mid-point (horizontal first, then vertical)
        int midX = x2;
        int midY = y1;

          Box m = new Box(w,h,d);

            Pos.setRecPosition(midX, midY, 1, 1, m);
             this.getChildren().add(m);

        // First horizontal leg
        Group leg1 = createEdge(x1, y1, midX, midY);

        // Second vertical leg
        Group leg2 = createEdge(midX, midY, x2, y2);

        this.getChildren().addAll(leg1, leg2);
    }

    /**
     * Build a cylinder edge between 2 grid points.
     */
    private Group createEdge(int x1, int y1, int x2, int y2) {
        Group g = new Group();

        int dx = x2 - x1;
        int dy = y2 - y1;

        int cells = Math.abs(dx) + Math.abs(dy) + 1;

        double radius = scale * 0.2;
        double height = cells * scale;

        Cylinder cyl = new Cylinder(radius, height);

        // Cylinder is vertical by default
        if (dy == 0) {
            // Horizontal → rotate 90° around Z
            cyl.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
        }

        // Left/upper-most cell is the anchor
        int startX = Math.min(x1, x2);
        int startY = Math.min(y1, y2);

        if (dy == 0) {
            Pos.setRecPosition(startX, startY, cells, 1, cyl);
        } else {
            Pos.setRecPosition(startX, startY, 1, cells, cyl);
        }

        g.getChildren().add(cyl);
        return g;
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

public WireGhost deepCopy() {
    WireGhost copy = new WireGhost();

    copy.scale = this.scale;

    // Clear initial empty children from constructor
    copy.getChildren().clear();

    // Recursive helper to clone all nodes
    copy.getChildren().addAll(cloneNodes(this.getChildren()));

    return copy;
}

/** Helper to recursively clone nodes */
private java.util.List<Node> cloneNodes(java.util.List<Node> nodes) {
    java.util.List<Node> clones = new java.util.ArrayList<>();
    for (Node node : nodes) {
        if (node instanceof Box b) {
            Box nb = new Box(b.getWidth(), b.getHeight(), b.getDepth());
            nb.setTranslateX(b.getTranslateX());
            nb.setTranslateY(b.getTranslateY());
            nb.setTranslateZ(b.getTranslateZ());
            nb.getTransforms().addAll(b.getTransforms());
            nb.setMaterial(b.getMaterial());
            nb.setOpacity(b.getOpacity());
            clones.add(nb);
        } 
        else if (node instanceof Cylinder c) {
            Cylinder nc = new Cylinder(c.getRadius(), c.getHeight());
            nc.setTranslateX(c.getTranslateX());
            nc.setTranslateY(c.getTranslateY());
            nc.setTranslateZ(c.getTranslateZ());
            nc.getTransforms().addAll(c.getTransforms());
            nc.setMaterial(c.getMaterial());
            nc.setOpacity(c.getOpacity());
            clones.add(nc);
        } 
        else if (node instanceof Group g) {
            Group ng = new Group();
            ng.getChildren().addAll(cloneNodes(g.getChildren())); // recursive
            ng.setTranslateX(g.getTranslateX());
            ng.setTranslateY(g.getTranslateY());
            ng.setTranslateZ(g.getTranslateZ());
            ng.getTransforms().addAll(g.getTransforms());
            clones.add(ng);
        }
    }
    return clones;
}

}
