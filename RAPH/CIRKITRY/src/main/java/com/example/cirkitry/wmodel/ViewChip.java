package com.example.cirkitry.wmodel;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.scale.Scale;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ViewChip extends Group 
{
     private final String id;  // stable ID
    private Component model;  // optional for committed mode
    private double scale =0;



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

        scale = Scale.WCellScale;

        double BoxH = scale*(w-2);
        double BoxW = scale*h;
        double BoxD = scale;

        Box body = new Box(BoxH,BoxW,BoxD);
        PhongMaterial bluePos = new PhongMaterial(Color.BLUE);
        body.setMaterial(bluePos);
         
        Pos.setRecPosition(x+1,y,w-2,h,body);

        this.getChildren().add(body);

        for(Pin p:comp.getInputPins())
        {
            Group g = new InputPin(scale);
            Pos.setRecPosition(p.getAbsoluteX(),p.getAbsoluteY(),1,1,g);
            this.getChildren().add(g);

        }

        
        for(Pin p:comp.getOutputPins())
        {
            Group g = new OutputPin(scale);
            Pos.setRecPosition(p.getAbsoluteX(),p.getAbsoluteY(),1,1,g);
            this.getChildren().add(g);

        }
         
    }






}
