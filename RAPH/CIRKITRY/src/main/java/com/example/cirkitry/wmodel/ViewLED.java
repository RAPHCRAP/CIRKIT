package com.example.cirkitry.wmodel;

import com.example.cirkitry.mathsutil.MathUtils;
import com.example.cirkitry.model.AbstractSink;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.scale.Scale;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class ViewLED extends Group implements SelectableView
{
     private final String id;  // stable ID
    private AbstractSink model;  // optional for committed mode
    private double scale =0;
    private Group status;



    public ViewLED(String id,AbstractSink comp)
    {
        this.id = id;
        this.model = comp;

     

        init();
    }

    private void init()
    {
        this.getChildren().clear();

        scale = Scale.WCellScale;
        int x = model.getX()+1;
        int y= model.getY()+1;

        int h=model.getHeight()-2;
        int w = model.getWidth()-2;

        Group LED = createBase();
        status = createBulb();

        LED.getChildren().add(status);

        setStatusColor(Color.AQUA);
        
        Pos.setRecPosition(x, y, w, h, LED);

        this.getChildren().add(LED);

        
        for(Pin p:model.getInputPins())
        {
            Group g = new InputPin(scale);
            Pos.setRecPosition(p.getAbsoluteX(),p.getAbsoluteY(),1,1,g);
            this.getChildren().add(g);

        }

        
         
    }

    private Group createBulb()
    {

        Group bulb = new Group();
        double radius = MathUtils.percentageValue(110, scale);
        double height = MathUtils.percentageValue(320, scale);
        Cylinder bell = new Cylinder(radius,height);
    
    bell.getTransforms().add(new Rotate(90,Rotate.X_AXIS));

double Sradius = MathUtils.percentageValue(110, scale);
    Sphere dome = new Sphere(Sradius);
dome.setTranslateZ(-80);

        bulb.getChildren().addAll(bell,dome);

        return bulb;
    }

    private Group createBase()
    {
          Group bulb = new Group();

          double radius = MathUtils.percentageValue(140, scale);
          double height = MathUtils.percentageValue(80, scale);
    Cylinder base = new Cylinder(radius,height);
    base.getTransforms().add(new Rotate(90,Rotate.X_AXIS));

    
    

    // Cylinder conn = new Cylinder(15,50);
    // conn.setTranslateY(-50);
    

    bulb.getChildren().addAll(base);

    return bulb;
    }

private void setStatusColor(Color color) 
{

    
    for (Node node : status.getChildren()) {
        if (node instanceof Shape3D) {
            Shape3D shape = (Shape3D) node;
            PhongMaterial material = new PhongMaterial(color);
            shape.setMaterial(material);
        }
        // For 2D shapes
        else if (node instanceof Shape) {
            Shape shape = (Shape) node;
            shape.setFill(color);
        }
    }
}



  @Override
    public Object getModel() {
        return model;
    }

    @Override
    public void onSelect() {
        setOpacity(1.0);
        // setColor(Color.CYAN);
    }

    @Override
    public void onDeselect() {
        // setOpacity(0.5);
        // setColor(Color.GRAY);
    }

    @Override
    public void rebuild() {
        init();
    }
    

     @Override
    public void addGroup(Group g) {
        this.getChildren().add(g);
    }


    @Override
    public void removeFromSubSceneRoot() {
    Parent parent = getParent();
    if (parent instanceof Group g) {
        g.getChildren().remove(this);
    }
}


}
