package com.example.cirkitry.wmodel;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public interface SelectableView {
    Object getModel();         // return Wire, Component, etc.
    void onSelect();           // highlight logic
    void onDeselect();         // remove highlight
    void rebuild();
    void addGroup(Group g);
    void removeFromSubSceneRoot();
    void update();
    
    void setColor(Color color);

}
