package com.example.cirkitry.wmodel;

import com.example.cirkitry.scale.Scale;

import javafx.scene.Node;

public class Pos {
    

    private static double scale = Scale.WCellScale;


    public static void setRecPosition(int x, int y, int w, int h, Node node) 
    {

    double unit = scale;   // or your cellSize variable

    double centerX = (x + w / 2.0) * unit;
    double centerY = (y + h / 2.0) * unit;
    double centerZ = 0;  // or whatever plane you want

    node.setTranslateX(centerX);
    node.setTranslateY(centerY);
    node.setTranslateZ(centerZ);
    }

}
