package com.example.cirkitry.wmodel;

import com.example.cirkitry.graphic.Arrow;
import com.example.cirkitry.mathsutil.MathUtils;

import javafx.scene.Group;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class OutputPin extends Group
{
    double scale =0;
    public OutputPin(double scale)
    {
        this.scale = scale;
        init();
    }

    private void init()
    {
    



        double h =MathUtils.percentageValue(80, scale);
        double w=MathUtils.percentageValue(80, scale);
        double d=MathUtils.percentageValue(80, scale);


        Box body = new Box(h,w,d);

        double ah =MathUtils.percentageValue(60, scale);
        double ab=MathUtils.percentageValue(60, scale);
        double ad=MathUtils.percentageValue(110, scale);


        Arrow arrow= new Arrow(h,w,d);

        arrow.setTranslateZ(-scale/2);

        arrow.getTransforms().add(new Rotate(-90,Rotate.Z_AXIS));

        this.getChildren().addAll(body,arrow);
        
        

        
    }
    
}
