package com.example.cirkitry.wmodel;
import javafx.scene.Group;

public interface SelectableView {
    Object getModel();         // return Wire, Component, etc.
    void onSelect();           // highlight logic
    void onDeselect();         // remove highlight
    void rebuild();
    void addGroup(Group g);
}
