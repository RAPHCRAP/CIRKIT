package com.example.cirkitry.wmodel;

import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.CompositeComponent;
import com.example.cirkitry.model.Primitive;

import javafx.scene.Group;
import javafx.scene.Node;


public class ViewBuilder {

    private final Group world;
    

    public ViewBuilder(Group root) {
        this.world = root;
    }

    public Group build(Circuit circuit) 
    {

        world.getChildren().removeAll();
        

        for (Component comp : circuit.getComponents()) 
        {
            

            
            Node model = buildComponentModel(comp);
            model.setUserData(comp); // link visual to logic
            world.getChildren().add(model);
        }

        // for (Wire wire : circuit.getWires()) {
        //     Node wireModel = buildWireModel(wire);
        //     wireModel.setUserData(wire);
        //     world.getChildren().add(wireModel);
        // }

        return world;
    }

    private Node buildComponentModel(Component comp) 
    {
        if (comp instanceof Primitive || comp instanceof CompositeComponent) 
        {
                
            return new ViewChip("",comp);
        } 
        // else if (comp instanceof Switch) 
        // {
        //     return new SwitchModel((Switch) comp);
        // } else if (comp instanceof PrimitiveGate) {
        //     return new GateModel((PrimitiveGate) comp);
        // } else if (comp instanceof CompositeComponent) {
        //     return new CompositeComponentModel((CompositeComponent) comp);
        // } 
        throw new RuntimeException("Unknown component type: " + comp.getClass());
    }

    // private Node buildWireModel(Wire wire) {
    //     // Use pin positions to create a line/curve
    //     return new WireModel(wire);
    // }
}