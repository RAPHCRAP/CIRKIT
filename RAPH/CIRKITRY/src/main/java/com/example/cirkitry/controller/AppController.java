package com.example.cirkitry.controller;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.example.cirkitry.EventHandles;
import com.example.cirkitry.Motion;
import  com.example.cirkitry.business.CircuitFileService;
import com.example.cirkitry.handler.GUIOverlay;
import com.example.cirkitry.handler.SelectHandler;
import com.example.cirkitry.mathsutil.TruthTable;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.ComponentFactory;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;



public class AppController {

    private Circuit circuit;
    
    private final SubScene subScene;
    private final Group world=new Group();

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Motion cameraMotion;
    private final SelectHandler selector;

    private final GUIOverlay gui;
    private final ComponentPaletteController paletteController;


    private final EventHandles eventHandle;

    private final Scene scene;

    private final CircuitFileService fileService = new CircuitFileService();

    

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


        // NEW HOOKS for menu commands
    gui.setOnNewRequested(this::handleNewRequest);
    gui.setOnOpenRequested(this::handleOpenRequest);
    gui.setOnSaveRequested(this::handleSaveRequest);
    gui.setOnSaveAsRequested(this::handleSaveAsRequest);
    gui.setOnExitRequested(() -> Platform.exit());


gui.setOnTruthTableRequested(x -> handleTruthTable());
gui.setOnExportImageRequested(this::handleExportImage);



    }

    private void handleExportImage() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Save Circuit Diagram");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
    File file = chooser.showSaveDialog(scene.getWindow());

    if (file != null) {
        // Take snapshot of subScene
        WritableImage image = subScene.snapshot(new SnapshotParameters(), null);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            gui.showMessage("Screenshot saved: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            gui.showMessage("Failed to save screenshot!");
        }
    }
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

private void handleTruthTable() {
    try {
        Circuit copy = this.circuit.deepCopy();
        TruthTable table = copy.generateTruthTable();
        


        gui.updateTruthTable(table);

    } catch (Exception ex) {
        gui.showMessage("Truth table failed: " + ex.getMessage());
    }
}



public void handleNewRequest() {
    // Reset file reference
    fileService.setCurrentFile(null);

    // Create a brand new empty circuit
    this.circuit = new Circuit(100, 100); // or whatever default size

    // Update selector
    world.getChildren().clear();
    selector.setCircuit(circuit);

    // // Rebuild world (clear and repopulate)
    // world.getChildren().clear();
    // rebuildWorldFromCircuit();

    gui.showMessage("New Circuit Created");
}


public void handleOpenRequest() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(new ExtensionFilter("Circuit Files", "*.json"));

    File file = chooser.showOpenDialog(scene.getWindow());
    if (file == null) return;

    try {
        Circuit loaded = fileService.load(file.toPath());
        this.circuit = loaded;

        world.getChildren().clear();
        selector.setCircuit(loaded);
        
        

    } catch (Exception ex) {
        gui.showMessage("Failed to open file: " + ex.getMessage());
    }
}


public void handleSaveAsRequest() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(new ExtensionFilter("Circuit Files", "*.json"));

    File file = chooser.showSaveDialog(scene.getWindow());
    if (file == null) return;

    try {
        fileService.save(file.toPath(), circuit);
        gui.showMessage("Saved");

    } catch (Exception ex) {
        gui.showMessage("Save failed: " + ex.getMessage());
    }
}

public void handleSaveRequest() {
    try {
        fileService.saveExisting(circuit);
        gui.showMessage("Saved");

    } catch (IllegalStateException noFile) {
        handleSaveAsRequest(); // fallback
    } catch (Exception ex) {
        gui.showMessage("Save failed: " + ex.getMessage());
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
    gui.showMessage("Placing new " + typeName);
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
