package com.example.cirkitry.wmodel;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.scale.Scale;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ViewChip extends Group 
{
     private final String id;  // stable ID
    private Component model;  // optional for committed mode

    public ViewChip(String id,Component comp)
    {
        this.id = id;
        this.model = comp;

     

        init(comp);
    }

    private void init(Component comp)
    {
        
        int x = comp.getX();
        int y = comp.getY();

        int w = comp.getWidth();
        int h = comp.getHeight();

        double scale = Scale.WCellScale;

        double BoxH = scale*(w-2);
        double BoxW = scale*h;
        double BoxD = scale;

        Box body = new Box(BoxH,BoxW,BoxD);
        PhongMaterial bluePos = new PhongMaterial(Color.BLUE);
        body.setMaterial(bluePos);
         
        setPosition(x+1,y,w-2,h,body);

        this.getChildren().add(body);

        // for(Pin p:comp.getInputPins())
        // {
        //     Group g = createInputPin();
        //     setPosition(p.get())
        // }
         
    }


    private void setPosition(int x, int y, int w, int h, Node node) {

    double unit = Scale.WCellScale;   // or your cellSize variable

    double centerX = (x + w / 2.0) * unit;
    double centerY = (y + h / 2.0) * unit;
    double centerZ = 0;  // or whatever plane you want

    node.setTranslateX(centerX);
    node.setTranslateY(centerY);
    node.setTranslateZ(centerZ);
}





}
