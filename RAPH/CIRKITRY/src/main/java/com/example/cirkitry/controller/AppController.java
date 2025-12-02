package com.example.cirkitry.controller;

import com.example.cirkitry.EventHandles;
import com.example.cirkitry.GUIOverlay;
import  com.example.cirkitry.Motion;
import com.example.cirkitry.handler.SelectHandler;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.ComponentFactory;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;



public class AppController {

    private final Circuit circuit;
    
    private final SubScene subScene;
    private final Group world=new Group();

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Motion cameraMotion;
    private final SelectHandler selector;

    private final GUIOverlay gui;
    private final ComponentPaletteController paletteController;


    private final EventHandles eventHandle;

    private final Scene scene;
    

    private enum Mode { EDIT, RUN }
    private Mode currentMode = Mode.EDIT;

    public AppController(Circuit c) 
    {
        this.circuit = c;
        
        addAxisBoxes(world);
        
        this.subScene = new SubScene(world, 1600, 900,true,SceneAntialiasing.BALANCED);
        this.subScene.setFill(Color.PURPLE);


        camera.setNearClip(0.1);
        camera.setFarClip(500000);

        Group cameraHolder = new Group(camera);
        cameraHolder.setTranslateZ(-3200);


        this.cameraMotion = new Motion(cameraHolder);


        this.subScene.setCamera(camera);



        this.selector = new SelectHandler(subScene, c);

        this.gui = new GUIOverlay();

        this. paletteController = new ComponentPaletteController(
                gui.getComponentSidebar()
        );

        StackPane root = new StackPane();

        root.getChildren().addAll(subScene,gui.getRoot());

        this.scene = new Scene(root,1600,900);
        this.eventHandle=new EventHandles(scene);



        





        selector.attachEventHandle(eventHandle);
        cameraMotion.attachEventHandle(eventHandle);
        cameraMotion.attachMouseEvent(this.scene);
        
        
         new AnimationTimer() {
            @Override
            public void handle(long now) {
                

                cameraMotion.update();
                selector.update();

            }
        }.start();
        

        hookGUIEvents();
        // hookWorldEvents();
    }

    private void hookGUIEvents()
    {
        

        // Subscribe to ComponentFactory updates
        ComponentFactory.addRegistryListener(paletteController::refreshPalette);

        // Handle button clicking (component placement)
        paletteController.setOnComponentSelected(this::handleComponentSelection);

        gui.setOnModeChanged(this::handleModeChange);

    }

    private void handleModeChange(GUIOverlay.Mode mode) {

    if (mode == GUIOverlay.Mode.RUN) {

        selector.enableRunMode();   //  disable editing
        applyRunModeFilter();       //  dim or blur subscene

    } else {

        selector.disableRunMode();  //  restore editing
        removeRunModeFilter();      //  remove subscene filter
    }
}

private void applyRunModeFilter()
{

}


private void removeRunModeFilter()
{
    
}

    private void handleComponentSelection(String typeName) {

    System.out.println("User selected: " + typeName);

    // 1. Create the component instance
    Component comp = ComponentFactory.create(typeName);

    System.err.println(comp.getType());
    // 2. Tell selector to start ADD mode
    selector.enableADD(comp);

    // 3. Optional: show message in GUI footer
    gui.updateMessage("Placing new " + typeName);
}


    public Scene getScene()
    {
        return scene;
    }

     public void addAxisBoxes(Group root) {
        double offset = 50;
        double boxSize = 5;

        // Positive axis colors
        PhongMaterial redPos = new PhongMaterial(Color.RED);
        PhongMaterial greenPos = new PhongMaterial(Color.LIME);
        PhongMaterial bluePos = new PhongMaterial(Color.BLUE);

        // Negative axis colors (darker)
        PhongMaterial redNeg = new PhongMaterial(Color.RED.darker());
        PhongMaterial greenNeg = new PhongMaterial(Color.LIME.darker());
        PhongMaterial blueNeg = new PhongMaterial(Color.BLUE.darker());

        // +X, -X
        Box boxPosX = new Box(boxSize, boxSize, boxSize);
        boxPosX.setTranslateX(offset);
        boxPosX.setMaterial(redPos);

        Box boxNegX = new Box(boxSize, boxSize, boxSize);
        boxNegX.setTranslateX(-offset);
        boxNegX.setMaterial(redNeg);

        // +Y, -Y
        Box boxPosY = new Box(boxSize, boxSize, boxSize);
        boxPosY.setTranslateY(offset);
        boxPosY.setMaterial(greenPos);

        Box boxNegY = new Box(boxSize, boxSize, boxSize);
        boxNegY.setTranslateY(-offset);
        boxNegY.setMaterial(greenNeg);

        // +Z, -Z
        Box boxPosZ = new Box(boxSize, boxSize, boxSize);
        boxPosZ.setTranslateZ(offset);
        boxPosZ.setMaterial(bluePos);

        Box boxNegZ = new Box(boxSize, boxSize, boxSize);
        boxNegZ.setTranslateZ(-offset);
        boxNegZ.setMaterial(blueNeg);

        root.getChildren().addAll(boxPosX, boxNegX, boxPosY, boxNegY, boxPosZ, boxNegZ);
    }
}
