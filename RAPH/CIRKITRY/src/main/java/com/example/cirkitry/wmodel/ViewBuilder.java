package com.example.cirkitry.wmodel;

import com.example.cirkitry.graphic.Board;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.CompositeComponent;
import com.example.cirkitry.model.Primitive;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.primitives.Led;
import com.example.cirkitry.model.primitives.Switch;

import javafx.scene.Group;
import javafx.scene.Node;

// import javafx.scene.image.Image;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.CullFace;
// import javafx.scene.shape.MeshView;
// import javafx.scene.shape.TriangleMesh;
// import javafx.scene.transform.Rotate;


public class ViewBuilder {

    private final Group world;
    private final Circuit circuit;

    

    public ViewBuilder(Group root,Circuit c) {
        
        this.world = root;
        this.circuit=c;
        
        
    }

    public Group build() 
    {

        world.getChildren().removeAll();
        
        world.getChildren().add(new Board(""));

        for (Component comp : circuit.getComponents()) 
        {
            addComponentView(comp);
        }

        for (Wire wire : circuit.getWires()) {
            addWireView(wire);
        }

        return world;
    }


    public void addWireView(Wire wire)
    {
         Node wireModel = buildWireModel(wire);
       
            
            if(wireModel instanceof SelectableView)
            {
                
                wire.setView((SelectableView)wireModel);
            }

            wireModel.setUserData(wire);
            world.getChildren().add(wireModel);
    }

    
    public void addComponentView(Component comp)
    {
         Node model = buildComponentModel(comp);
            
            
            if(model instanceof  SelectableView)
            {
                comp.setView((SelectableView)model);
            }



            model.setUserData(comp); // link visual to logic
            world.getChildren().add(model);
    }

    private Node buildComponentModel(Component comp) 
    {
        if (comp instanceof Primitive || comp instanceof CompositeComponent) 
        {
                
            return new ViewChip("",comp);
        }
        else if(comp instanceof Led)
        {
            
            return new ViewLED("", (Led)comp);
            
        }
        else if (comp instanceof Switch) 
        {
            return new ViewSwitch("",(Switch)comp);
        } 
        //else if (comp instanceof PrimitiveGate) {
        //     return new GateModel((PrimitiveGate) comp);
        // } else if (comp instanceof CompositeComponent) {
        //     return new CompositeComponentModel((CompositeComponent) comp);
        // } 
        throw new RuntimeException("Unknown component type: " + comp.getClass());
    }

    private Node buildWireModel(Wire wire) {
        // Use pin positions to create a line/curve
        return new WireModel("",wire);
    }

 
}