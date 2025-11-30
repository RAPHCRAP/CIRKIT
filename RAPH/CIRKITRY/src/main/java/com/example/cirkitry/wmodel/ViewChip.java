package com.example.cirkitry.wmodel;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.scale.Scale;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ViewChip extends Group implements SelectableView
{
     private final String id;  // stable ID
    private Component model;  // optional for committed mode
    private double scale =0;



    public ViewChip(String id,Component comp)
    {
        this.id = id;
        this.model = comp;

     

        init();
    }

    private void init()
    {
        
        int x = model.getX();
        int y = model.getY();

        int w = model.getWidth();
        int h = model.getHeight();

        scale = Scale.WCellScale;

        double BoxH = scale*(w-2);
        double BoxW = scale*h;
        double BoxD = scale;

        Box body = new Box(BoxH,BoxW,BoxD);
        PhongMaterial bluePos = new PhongMaterial(Color.BLUE);
        body.setMaterial(bluePos);
         
        Pos.setRecPosition(x+1,y,w-2,h,body);

        this.getChildren().add(body);

        for(Pin p:model.getInputPins())
        {
            Group g = new InputPin(scale);
            Pos.setRecPosition(p.getAbsoluteX(),p.getAbsoluteY(),1,1,g);
            this.getChildren().add(g);

        }

        
        for(Pin p:model.getOutputPins())
        {
            Group g = new OutputPin(scale);
            Pos.setRecPosition(p.getAbsoluteX(),p.getAbsoluteY(),1,1,g);
            this.getChildren().add(g);

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
    



}
