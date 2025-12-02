package com.example.cirkitry.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.cirkitry.EventHandles;
import com.example.cirkitry.controller.CompositeComponentController;
import com.example.cirkitry.debuging.ComponentPrinter;
import com.example.cirkitry.model.Cell;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.ComponentBuilder;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.WireNode;
import com.example.cirkitry.model.primitives.Switch;
import com.example.cirkitry.scale.Scale;
import com.example.cirkitry.wmodel.ViewBuilder;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
  

public class SelectHandler 
{
    private SubScene scene3D;
    private Camera camera;
    private Parent root;
    private double mouseX, mouseY;
    private Circuit circuit;
     private Box selectionBox;
     private Highlight cellHighlight;
    private double cellSize = Scale.WCellScale; 

    private WireNode node=null;
    private int selectedCellX=0;
    private int selectedCellY=0;

    private int startHighlightX=0;
    private int startHighlightY=0;

    private int hoveringCellX=0;
    private int hoveringCellY=0;

    private WireGhost tmpWire;
    private Wire selectedWire;

    private ComponentGhost tmpComp;
    private Component selectedComponent;

    private EditorMode mode=EditorMode.NONE;

    

     private EventHandles activeKeys;
     private ViewBuilder vb; 
    
     private boolean ctrlDown = false;


    
    
    public SelectHandler(SubScene scene3D,Circuit circuit) 
    {
        this.scene3D = scene3D;
        this.camera = scene3D.getCamera();
        this.root = scene3D.getRoot();
        this.circuit = circuit;

        tmpWire = new WireGhost();
        tmpComp = new ComponentGhost();
        
        if(this.root instanceof Group)
        {
            vb = new ViewBuilder((Group)this.root,circuit);
            vb.build();
        }
        
           // Initialize the selection box
        selectionBox = createSelectionBox();

        cellHighlight = new Highlight();
        cellHighlight.setVisible(false);
        if (root instanceof Group) 
            {

        ((Group) root).getChildren().add(selectionBox);
        ((Group) root).getChildren().add(cellHighlight);
        ((Group) root).getChildren().add(tmpWire);
         ((Group) root).getChildren().add(tmpComp);                   

         } else 
            {
    System.err.println("SubScene root is not a Group, cannot add selection box!");
              } 

        selectionBox.setVisible(false); // Hide initially
        tmpWire.setOpacity(0.5);
        tmpWire.setVisible(false);
        tmpComp.setOpacity(0.5);
        tmpComp.setVisible(false);

        
        setupMouseHandlers();
    }

    private Box createSelectionBox() {
        Box box = new Box(cellSize, cellSize, 10); // Thin box to visualize XY plane
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(Color.rgb(255, 255, 0, 0.4)); // Semi-transparent yellow
        mat.setSpecularColor(Color.TRANSPARENT);
        box.setMaterial(mat);
        return box;
    }

    public void attachEventHandle(EventHandles e)
    {

        this.activeKeys =e;
    }
    private void setupMouseHandlers() {
        scene3D.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            
            updateSelection();
        });

        scene3D.setOnMousePressed(event -> {
            ctrlDown = event.isControlDown();
        });
        
        scene3D.setOnMouseClicked(event -> {
           
            MouseButton btn = event.getButton();

    switch (btn) {
        case PRIMARY -> { 
             mouseX = event.getX();
            mouseY = event.getY();

            
            

            
            
            handleSelection();
            break;
        }
        case SECONDARY -> { 
            releaseSelectionHandle();
            break;
        }
        case MIDDLE -> {
            releaseSelectionHandle();
            break;
        }
    }
          
        });
    }


     private void updateSelection() {
        Point3D worldPos = screenToWorld(mouseX, mouseY);
        
        
        if (worldPos != null) {

            Point3D cell = worldToGridCell(worldPos);

            hoveringCellX = (int)cell.getX();
            hoveringCellY = (int)cell.getY();

            // Update selection box position
            placeSelectionBox(cell);


            
            switch(mode)
            {
                case WIRE_NODE_SELECTED:

                    temporaryWirePlacement(hoveringCellX, hoveringCellY);

                break;

                case COMPONENT_SELECTED:
                    
                    temporaryComponentPlacement(hoveringCellX, hoveringCellY);

                break;

                case WIRE_ADD:

                    temporaryWireAdd(hoveringCellX, hoveringCellY);

                break;

                case HIGHLIGHT:

                    temporaryHighLight(hoveringCellX,hoveringCellY);
                break;

                case ADD_COMPONENT:
                    temporaryComponentPlacement(hoveringCellX, hoveringCellY);
                break;
            }

            // Optional: call your actual selection logic
            // selectAtPosition(cell);
        }
    }
    
    private void handleSelection() {
        Point3D worldPos = screenToWorld(mouseX, mouseY);
     
        
        if (worldPos != null) {



            
            // Perform actual selection
             Point3D coord = worldToGridCell(worldPos);


            //  selectAtPosition(coord);
             int gridX= (int)coord.getX();
            int gridY= (int)coord.getY();

            selectedCellX = gridX;
                    selectedCellY = gridY; 
             
             Cell c = circuit.getCell(gridX, gridY);

            
             if(mode==EditorMode.NONE)
             {  
                updateMode();

             }
             else if (mode==EditorMode.COMPONENT_SELECTED) {
                  moveComponent();
             }
             else if (mode==EditorMode.WIRE_NODE_SELECTED) {
                 wireExtensionHandle();
             }
             else if(mode==EditorMode.WIRE_ADD)
             {
                addWireAcrossNode();
             }
             else if(mode==EditorMode.HIGHLIGHT)
             {
                selectHighlight();
             }
             else if(mode==EditorMode.RUN_MODE)
             {
                toggleSwitches();
             }
             else if(mode==EditorMode.ADD_COMPONENT)
             {
                addComponent();
             }
            
          

             

           

            


            

            


        }
    }

    private void updateMode()
    {   
        Cell c = circuit.getCell(selectedCellX, selectedCellY);

        if(c.hasPin()&&c.getPin().isOutput()&&c.getPin().getConnections().isEmpty())
        {
            // HAS OUTPIN WITH NO CONNECTIONS
            tmpWire.setVisible(true);
            selectedWire = new Wire(selectedCellX,selectedCellY,c.getPin());
            mode = EditorMode.WIRE_ADD;
            return;
        }

         if(c.hasNode()&&c.getNode().getDegree()<4)
        {
                 node = c.getNode();
                    selectedWire = node.getWire();
                    tmpWire.setVisible(true);
                    
                    mode = EditorMode.WIRE_NODE_SELECTED;
                    return;
                             

        }

        if(c.hasComponent())
        {
            selectedComponent = c.getComponent();
            ComponentPrinter.printComponent(selectedComponent);
            tmpComp.setVisible(true);
            mode = EditorMode.COMPONENT_SELECTED;
            return;

        }

        if(c.isClear()&&ctrlDown)
        {
            
            mode = EditorMode.HIGHLIGHT;
            cellHighlight.setVisible(true);
            startHighlightX = selectedCellX;
            startHighlightY = selectedCellY;
            return;
        }
        
       
    }

    public void update()
    {
        if(activeKeys!=null)
        {
            KeyPressHandles();
        }
         
       
    }

    private void KeyPressHandles()
    {
         if(activeKeys.contains(KeyCode.ESCAPE))
        {
            
            releaseSelectionHandle();

        }
        if(activeKeys.contains(KeyCode.BACK_SPACE))
        {
            switch(mode)
            {
                case WIRE_NODE_SELECTED :
                    node.getWire().deleteNode(node, circuit);
                    node.getWire().rebuild();
                break;


                case COMPONENT_SELECTED :
                    circuit.removeComponent(selectedComponent);
                break;

                
            }

            releaseSelectionHandle();
           
        }
    }

    private void moveComponent()
    {


         if(selectedComponent.getX()==hoveringCellX&&selectedComponent.getY()==hoveringCellY)
            {
                
                // DO NOTHING
               
                
            }
            else if(selectedComponent.canMoveTo(hoveringCellX, hoveringCellY, circuit))
            {   
             
                selectedComponent.moveTo(hoveringCellX, hoveringCellY, circuit);
                selectedComponent.rebuild();
                
            }
            
            
            releaseSelectionHandle();
            

            
    }

    private void wireExtensionHandle()
    {
         if(node.getWire().extendEdge(node, selectedCellX, selectedCellY, circuit))
            {
                    
                selectedWire.getView().addGroup(tmpWire.deepCopy());
                    
                    
                   
                    
            }
                
            releaseSelectionHandle();

            
    }

    private void addWireAcrossNode()
    {

        if(circuit.addWire(selectedWire))
        {
            
            selectedWire.extendEdge(selectedWire.getNodes().get(0), selectedCellX, selectedCellY, circuit);
            vb.addWireView(selectedWire);
        }
        releaseSelectionHandle();
    }

    private CompositeComponentController compositeController = new CompositeComponentController();

private void selectHighlight() {

String typeName = askUserForComponentName();
    if (typeName == null || typeName.isBlank()) {
        releaseSelectionHandle();
        return;
    }

    ComponentBuilder builder = circuit.extractCompositeFromRect(
        startHighlightX,
        startHighlightY,
        selectedCellX,
        selectedCellY,
        typeName
    );

    

    try {
        compositeController.registerCompositeComponent(typeName, builder);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }

    releaseSelectionHandle();
}

private String askUserForComponentName() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Composite Component");
    dialog.setHeaderText("Enter a name for the new component:");
    Optional<String> result = dialog.showAndWait();
    return result.orElse(null);
}



     private void toggleSwitches()
    {
        Cell c = circuit.getCell(selectedCellX, selectedCellY);

        if(c.hasComponent()&&c.getComponent() instanceof Switch)
        {
            System.err.println("HERE");
            Switch sw = (Switch)c.getComponent();
            sw.toggle();
            

        }
    }


    private void addComponent()
    {
        if(circuit.addComponent(hoveringCellX, hoveringCellY, selectedComponent))
        {
            vb.addComponentView(selectedComponent);
        }

        releaseSelectionHandle();
    }
public void enableRunMode()
{
    releaseSelectionHandle();
    mode = EditorMode.RUN_MODE;
}

public void disableRunMode()
{
    releaseSelectionHandle();
    mode = EditorMode.NONE;
}

public void enableADD(Component comp)
{
    System.err.println("ENABLE ADD::"+comp.getType());
    
    System.err.printf("%d:%d\n",comp.getWidth(),comp.getHeight());

    releaseSelectionHandle();
    selectedComponent=comp;
    tmpComp.setVisible(true);
    mode = EditorMode.ADD_COMPONENT;

    
}
    

    private void releaseSelectionHandle()
    {
                    mode = EditorMode.NONE;
                    node = null;

                    tmpWire.setVisible(false);
                    tmpWire.getChildren().clear();
                    selectedWire=null;


                    
                    tmpComp.setVisible(false);
                    tmpComp.getChildren().clear();
                    selectedComponent = null;

                    cellHighlight.setVisible(false);
    }

    private void temporaryWirePlacement(int nx,int ny)
    {
        
        tmpWire.update(selectedCellX, selectedCellY, nx, ny);
        tmpWire.setOpacity(1);

        
        if(selectedWire.canExtend(node, nx, ny, circuit))
        {
            tmpWire.setColor(Color.AQUA);
        }
        else{
            tmpWire.setColor(Color.RED);
        }
        
        
    }

    
    private void temporaryWireAdd(int nx,int ny)
    {
        
        tmpWire.update(selectedCellX, selectedCellY, nx, ny);
        tmpWire.setOpacity(1);

        
        if(selectedWire.canPlace(circuit))
        {
            tmpWire.setColor(Color.AQUA);
        }
        else{
            tmpWire.setColor(Color.RED);
        }
        
        
    }

    private void temporaryComponentPlacement(int x, int y)
    {
        tmpComp.update(x, y, selectedComponent.getWidth(), selectedComponent.getHeight());
        tmpComp.setOpacity(1);


        if(selectedComponent.canMoveTo(x, y, circuit))
        {
            tmpComp.setColor(Color.AQUA);
        }
        else{
            tmpComp.setColor(Color.RED);
        }
    }

    private void temporaryHighLight(int x, int y)
    {
        cellHighlight.update(startHighlightX,startHighlightY, x, y);
        cellHighlight.setOpacity(1);


    }

    private Point3D getMouseCell()
    {
       return worldToGridCell(screenToWorld(mouseX, mouseY));

    }
    
private Point3D worldToGridCell(Point3D worldPos) {
    if (worldPos == null) return null;

    int cellX = (int) Math.floor(worldPos.getX() / cellSize);
    int cellY = (int) Math.floor(worldPos.getY() / cellSize);

    return new Point3D(cellX, cellY, 0);
}

private void placeSelectionBox(Point3D cell) {
        if (cell == null) return;

        double centerX = cell.getX() * cellSize + cellSize / 2.0;
        double centerY = cell.getY() * cellSize + cellSize / 2.0;

        selectionBox.setTranslateX(centerX);
        selectionBox.setTranslateY(centerY);
        selectionBox.setTranslateZ(0); // On Z=0 plane

        selectionBox.setVisible(true);
    }

    private void highlightAtPosition(Point3D worldPos) {
        // Find nodes near this world position
        // You can raycast or use spatial partitioning
        for (Node node : getAllSelectableNodes()) {
            if (isNodeAtPosition(node, worldPos)) {
                // Highlight logic
                node.setEffect(new Glow(0.5));
            }
        }
    }
    
    private void selectAtPosition(Point3D worldPos) {
        // // Your selection logic here
        System.out.printf("Selected at: (%.2f, %.2f, %.2f)\n", 
            worldPos.getX(), worldPos.getY(), worldPos.getZ());
    }
    
    private List<Node> getAllSelectableNodes() {
        List<Node> selectable = new ArrayList<>();
        collectSelectableNodes(root, selectable);
        return selectable;
    }
    
    private void collectSelectableNodes(Node node, List<Node> selectable) {
        if (node instanceof MeshView || node instanceof Group) {
            selectable.add(node);
        }
        
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                collectSelectableNodes(child, selectable);
            }
        }
    }
    
    private boolean isNodeAtPosition(Node node, Point3D worldPos) {
        // Simple bounding box check - you can make this more sophisticated
        Bounds bounds = node.getBoundsInParent();
        return bounds.contains(worldPos.getX(), worldPos.getY(), worldPos.getZ());
    }


    
    public Point3D screenToWorld(double screenX, double screenY) {
    if (camera instanceof PerspectiveCamera) {
        return screenToWorldPerspective(screenX, screenY);
    } 
    if (camera instanceof ParallelCamera) {
        return screenToWorldOrthographic(screenX, screenY);
    }
    return null;
}
private Point3D screenToWorldPerspective(double screenX, double screenY) {
    if (!(camera instanceof PerspectiveCamera)) return null;

    // --- 1. Camera position and basis vectors ---
    Point3D camPos = camera.localToScene(Point3D.ZERO);                // camera world position
    Point3D forward = toWorldDirection(new Point3D(0, 0, 1)).normalize(); // camera forward
    Point3D up = toWorldDirection(new Point3D(0, -1, 0)).normalize();       // camera up
    Point3D right = forward.crossProduct(up).normalize();                  // camera right

    // --- 2. Normalized screen coordinates [-1, 1] ---
    double nx = (screenX / scene3D.getWidth()) * 2 - 1;      // -1 left, +1 right
    double ny = 1 - (screenY / scene3D.getHeight()) * 2;     // +1 top, -1 bottom

    // --- 3. Apply FOV to compute angular offsets ---
    double fovY = Math.toRadians(((PerspectiveCamera)camera).getFieldOfView());
    double aspect = scene3D.getWidth() / scene3D.getHeight();
    double fovX = 2 * Math.atan(Math.tan(fovY / 2) * aspect);

    double offsetX = nx * Math.tan(fovX / 2);
    double offsetY = ny * Math.tan(fovY / 2);

    // --- 4. Compute ray direction in world space ---
    Point3D rayDir = forward
            .add(right.multiply(offsetX))
            .add(up.multiply(offsetY))
            .normalize();

    // --- 5. Intersect ray with Z=0 plane ---
    Point3D planeNormal = new Point3D(0, 0, 1); // Z=0 plane normal
    double denom = rayDir.dotProduct(planeNormal);
    if (Math.abs(denom) < 1e-10) return null; // ray is parallel to plane

    double t = -camPos.getZ() / denom;
    if (t < 0) return null; // intersection behind camera

    return camPos.add(rayDir.multiply(t));
}


private Point3D screenToWorldOrthographic(double screenX, double screenY) {
    if (!(camera instanceof ParallelCamera)) return null;

    // --- 1. Camera position and basis vectors ---
    Point3D camPos = camera.localToScene(Point3D.ZERO);                // camera world position
    Point3D forward = toWorldDirection(new Point3D(0, 0, 1)).normalize(); // camera forward
    Point3D up = toWorldDirection(new Point3D(0, -1, 0)).normalize();       // camera up
    Point3D right = forward.crossProduct(up).normalize();                  // camera right

    // --- 2. Normalized screen coordinates [-1, 1] ---
    double nx = (screenX / scene3D.getWidth()) * 2 - 1;      // -1 left, +1 right
    double ny = 1 - (screenY / scene3D.getHeight()) * 2;     // +1 top, -1 bottom

    // --- 3. Orthographic screen scale (world units) ---
    double orthoWidth = 20;   // customize as needed
    double orthoHeight = 20;  // customize as needed

    // --- 4. Compute ray origin in world space ---
    Point3D rayOrigin = camPos
            .add(right.multiply(nx * orthoWidth / 2))
            .add(up.multiply(ny * orthoHeight / 2));

    // --- 5. Ray direction is camera forward ---
    Point3D rayDir = forward;

    // --- 6. Intersect ray with Z=0 plane ---
    Point3D planeNormal = new Point3D(0, 0, 1); // Z=0 plane
    double denom = rayDir.dotProduct(planeNormal);
    if (Math.abs(denom) < 1e-10) return null; // ray parallel → no intersection

    double t = -rayOrigin.getZ() / denom;
    if (t < 0) return null; // intersection behind camera

    return rayOrigin.add(rayDir.multiply(t));
}



private Point3D toWorldDirection(Point3D camDir) {

    // world position of camera origin
    Point3D origin  = camera.localToScene(Point3D.ZERO);

    // world position of a point one unit in camDir direction
    Point3D end     = camera.localToScene(camDir);

    // subtract to get real-world direction
    return end.subtract(origin).normalize();
}

    // YAY
}